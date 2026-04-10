package com.retalho.vendas.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.retalho.vendas.model.Order;
import com.retalho.vendas.model.Produto;
import com.retalho.vendas.model.ProdutoOrder;
import com.retalho.vendas.model.UserDtls;
import com.retalho.vendas.service.CartService;
import com.retalho.vendas.service.OrderService;
import com.retalho.vendas.service.ProdutoService;
import com.retalho.vendas.service.UploadService;
import com.retalho.vendas.service.UserService;
import com.retalho.vendas.util.CommonUtil;
import com.retalho.vendas.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
    @Autowired
    private UploadService uploadService;

	@GetMapping("/")
	public String index() {

		return "admin/index";
	}

	@GetMapping("/addProduto")
	public String addProduto() {

		return "/admin/adicionar_produto";
	}
	@PostMapping("/salvarProduto")
    public String salvarProduto(@ModelAttribute Produto produto,
                                @RequestParam("file1") MultipartFile imagem1,
                                @RequestParam("file2") MultipartFile imagem2,
                                @RequestParam("file3") MultipartFile imagem3,
                                HttpSession session) {
        try {
            List<String> nomesImagens = new ArrayList<>();
            MultipartFile[] imagens = { imagem1, imagem2, imagem3 };

            for (MultipartFile img : imagens) {
                if (!img.isEmpty()) {
                    // Chamamos a interface!
                    String nomeSalvo = uploadService.salvarImagem(img, "produto_img");
                    nomesImagens.add(nomeSalvo);
                }
            }

            produto.setImagens(nomesImagens);
            Produto salvarProduto = produtoService.salvarProduto(produto);

            if (salvarProduto != null) {
                session.setAttribute("succMsg", "Produto Salvo com Sucesso");
            } else {
                session.setAttribute("errorMsg", "Erro no Servidor");
            }

        } catch (IOException e) {
            session.setAttribute("errorMsg", "Erro ao processar imagens: " + e.getMessage());
        }

        return "redirect:/admin/addProduto";
    }


	@GetMapping("/verProduto")
	public String verProduto(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize,
			@RequestParam(name = "ch", required = false) String searchTerm) {

		Page<Produto> page;

		if (searchTerm != null && !searchTerm.trim().isEmpty()) {
			// Se há termo de busca, usar busca paginada
			page = produtoService.searchProdutosPagination(searchTerm, pageNo, pageSize);
			m.addAttribute("searchTerm", searchTerm);
		} else {
			// Listagem normal paginada
			page = produtoService.getAllProdutoPaginationAdmin(pageNo, pageSize);
		}

		m.addAttribute("produto", page.getContent());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "/admin/verProduto";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable Long id, HttpSession session) {
		Boolean deleteProduct = produtoService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Produto deletado com sucesso");
		} else {
			session.setAttribute("errorMsg", "ERRO");
		}
		return "redirect:/admin/verProduto";
	}

	@GetMapping("/editarProduto/{id}")
	public String editarProduto(@PathVariable Long id, Model m) {
		m.addAttribute("produto", produtoService.getProductById(id));
		return "/admin/editar_produto";
	}

	@PostMapping("/atualizarProduto")
	public String atualizarProduto(@ModelAttribute Produto produto, @RequestParam("file1") MultipartFile imagem1,
			@RequestParam("file2") MultipartFile imagem2, @RequestParam("file3") MultipartFile imagem3,
			HttpSession session) {

		Produto atualizado = produtoService.atualizarProduto(produto, imagem1, imagem2, imagem3);

		if (atualizado != null) {
			session.setAttribute("succMsg", "Produto atualizado com sucesso!");
		} else {
			session.setAttribute("errorMsg", "Erro ao atualizar o produto.");
		}

		return "redirect:/admin/editarProduto/" + produto.getId();
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {

		// 'Principal' representa o usuário autenticado no Spring Security.
		// Se ninguém estiver logado, o 'p' será null.
		if (p != null) {
			// Obtém o nome do usuário logado.
			// No caso do Spring Security, normalmente é o e-mail ou username.
			String email = p.getName();

			// Busca no banco de dados os detalhes completos do usuário
			// usando o e-mail obtido do Principal.
			UserDtls userDtls = userService.getUserbyEmail(email);

			// Adiciona o objeto 'user' ao Model.
			// Assim, qualquer página renderizada poderá acessar ${user}
			// e mostrar informações como nome, e-mail, etc.
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
	}

	@GetMapping("/usuarios")
	public String getAllUsers(Model m,@RequestParam Integer type) {
		List<UserDtls> users = userService.getUsers("ROLE_USER");
		if(type==1) {
			 users =userService.getUsers("ROLE_USER");
	}else {
		 users =userService.getUsers("ROLE_ADMIN");
	}
		m.addAttribute("usuariosType", type);
		m.addAttribute("usuarios", users);
		return "/admin/usuarios";
	}

	@GetMapping("/atualizarStatus")
	public String UpdateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,@RequestParam Integer type, HttpSession session) {

		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Status da Conta atualizado");
		} else {
			session.setAttribute("errorMsg", "Erro no Servidor");
		}
		return "redirect:/admin/usuarios?type="+type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize) {

		Page<Order> page = orderService.getAllOrdersPagination(pageNo, pageSize);

		m.addAttribute("orders", page.getContent());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		m.addAttribute("srch", false);

		return "/admin/orders";
	}

	@PostMapping("/update-status")
	public String updateOrderStatus(@RequestParam Long id, @RequestParam Integer st, HttpSession session) {
		OrderStatus[] values = OrderStatus.values();
		String status = null;
		for (OrderStatus orderStatus : values) {
			if (orderStatus.getId().equals(st)) {
				status = orderStatus.getName();
			}
		}

		Order updateOrderStatus = orderService.updateOrderStatus(id, status);
		try {
			commonUtil.sendMailForOrder(updateOrderStatus, status);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		if (!ObjectUtils.isEmpty(updateOrderStatus)) {
			session.setAttribute("succMsg", "Status atualizado");
		} else {
			session.setAttribute("errorMsg", "Status não atualizado");
		}

		return "redirect:/admin/orders";
	}

	@GetMapping("/search-order")
	public String searchOrder(@RequestParam String orderId,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Model m, HttpSession session) {

		if (orderId != null && !orderId.trim().isEmpty()) {
			// Tenta buscar pedido específico primeiro
			Order exactOrder = orderService.getOrderByOrderId(orderId.trim());

			if (exactOrder != null) {
				// Pedido exato encontrado - mantém comportamento original
				m.addAttribute("orderDtls", exactOrder);
				m.addAttribute("srch", true);
			} else {
				// Não encontrou pedido exato, faz busca paginada
				Page<Order> page = orderService.searchOrdersPagination(orderId.trim(), pageNo, pageSize);

				if (page.getTotalElements() == 0) {
					session.setAttribute("errorMsg", "Nenhum pedido encontrado para: " + orderId);
				} else {
					m.addAttribute("orders", page.getContent());
					m.addAttribute("pageNo", page.getNumber());
					m.addAttribute("pageSize", pageSize);
					m.addAttribute("totalElements", page.getTotalElements());
					m.addAttribute("totalPages", page.getTotalPages());
					m.addAttribute("isFirst", page.isFirst());
					m.addAttribute("isLast", page.isLast());
					m.addAttribute("searchTerm", orderId);
				}
				m.addAttribute("srch", true);
			}
		} else {
			// orderId vazio - retorna lista paginada geral
			return "redirect:/admin/orders";
		}
		return "/admin/orders";
	}
	
	@GetMapping("/add-admin")
	public String loadAdminAdd(Model m) {
		m.addAttribute("paginaComFundoRetalhos", true);
		return "admin/add_admin";
	}
	
	@PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute UserDtls user,
                           @RequestParam("confirmarSenha") String confirmarSenha,
                           HttpSession session) {

        // Verifica se as senhas coincidem
        if (!user.getSenha().equals(confirmarSenha)) {
            session.setAttribute("errorMsg", "As senhas não coincidem.");
            return "redirect:/admin/add-admin";
        }

        // Salva o usuário
        UserDtls saveUser = userService.saveAdmin(user);

        if (saveUser != null) {
            session.setAttribute("succMsg", "Administrador salvo com sucesso!");
        } else {
            session.setAttribute("errorMsg", "Erro ao salvar Adminstrador.");
        }

        return "redirect:/admin/add-admin";
    }
	
	@GetMapping("/profile")
	public String profile() {
		return "admin/profile";
	}
	
	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user);
		if(ObjectUtils.isEmpty(updateUserProfile)) {
			
			session.setAttribute("errorMsg", "Perfil não atualizado");
		}else {
			 session.setAttribute("succMsg", "Perfil atualizado");
		}
		return  "redirect:/admin/profile";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,Principal p,HttpSession session) {
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);
		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getSenha());
		if(matches) {
			String encode = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setSenha(encode);
			UserDtls updateUserPassword = userService.updateUser(loggedInUserDetails);
			if(ObjectUtils.isEmpty(updateUserPassword)) {
				session.setAttribute("errorMsg", "Senha não atualizada! Erro no servidor!");
			}else {
				 session.setAttribute("succMsg", "Senha atualizada");
			}
		}else {
			session.setAttribute("errorMsg", "Senha atual errada! Informe a senha correta!");
		}
		
		return  "redirect:/admin/profile";
	}

}
