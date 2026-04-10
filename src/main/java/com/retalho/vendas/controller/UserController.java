package com.retalho.vendas.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.retalho.vendas.model.Cart;
import com.retalho.vendas.model.CartItem; // Importado
import com.retalho.vendas.model.Endereco;
import com.retalho.vendas.model.Order;
import com.retalho.vendas.model.OrderRequest;
import com.retalho.vendas.model.UserDtls;
import com.retalho.vendas.repository.CartItemRepository; // Adicionado
import com.retalho.vendas.repository.CartRepository;
import com.retalho.vendas.repository.EnderecoRepository;
import com.retalho.vendas.service.CartService;
import com.retalho.vendas.service.OrderService;
import com.retalho.vendas.service.UserService;
import com.retalho.vendas.util.CommonUtil;
import com.retalho.vendas.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository; // Precisamos dele para verificar duplicidade

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Autowired
	private EnderecoRepository enderecoRepository;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserbyEmail(email);
			m.addAttribute("user", userDtls);

			// O countCart agora retorna a soma dos itens do carrinho
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
	}

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Long pid, @RequestParam Long uid, RedirectAttributes redirectAttributes) {

		// Correção: Agora buscamos o carrinho do usuário primeiro
		Cart cart = cartRepository.findByUserId(uid);

		// Verifica se o ITEM já existe dentro desse carrinho
		CartItem existente = null;
		if (cart != null) {
			existente = cartItemRepository.findByCartIdAndProdutoId(cart.getId(), pid);
		}

		if (existente != null) {
			redirectAttributes.addFlashAttribute("warnMsg",
					"Esse produto já está no seu carrinho. O limite é 1 unidade.");
		} else {
			// saveCart agora retorna um CartItem
			CartItem novo = cartService.saveCart(pid, uid);
			if (ObjectUtils.isEmpty(novo)) {
				redirectAttributes.addFlashAttribute("errorMsg", "Erro ao adicionar o produto");
			} else {
				redirectAttributes.addFlashAttribute("succMsg", "Produto adicionado ao carrinho");
			}
		}

		return "redirect:/produto/" + pid;
	}

	@GetMapping("/carrinho")
	public String loadCartPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);

		// Correção: Agora recebemos uma lista de CartItem
		List<CartItem> cartItems = cartService.getCartsbyUser(user.getId());

		// Calculando o total para exibir no carrinho (opcional, mas ajuda o usuário)
		// BigDecimal é usado para evitar erros de arredondamento em cálculos
		// financeiros
		BigDecimal totalCarrinho = cartItems.stream() // 1. Transforma a lista de itens em um "fluxo" de dados para
														// processamento

				// 2. Pega cada 'item' do carrinho e extrai apenas o valor que nos interessa (o
				// preço)
				// Isso transforma o Stream<CartItem> em um Stream<BigDecimal>
				.map(item -> item.getPriceAtAdd())

				// 3. Soma todos os valores do fluxo.
				// BigDecimal.ZERO é o valor inicial (o acumulador começa em 0)
				// BigDecimal::add é a operação que diz: "pegue o acumulador e some com o
				// próximo valor"
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		m.addAttribute("carts", cartItems); // Mantivemos o nome "carts" para não quebrar o HTML, mas são itens
		m.addAttribute("totalCarrinho", totalCarrinho);

		return "user/cart";
	}

	@GetMapping("/carrinhoDelete")
	public String removeProductCart(@RequestParam Long cid, HttpSession session) {
		// cid aqui é o ID do CartItem
		Boolean deletCart = cartService.delectCartProduct(cid);
		if (deletCart) {
			session.setAttribute("succMsg", "Item removido com sucesso");
		} else {
			session.setAttribute("errorMsg", "Erro ao remover o produto!");
		}

		return "redirect:/user/carrinho";
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);

		// Buscamos os itens diretamente pelo service
		List<CartItem> cartItems = cartService.getCartsbyUser(user.getId());
		List<Endereco> enderecosSalvos = enderecoRepository.findByUserId(user.getId());
	    

		BigDecimal subtotal = cartItems.stream()
				.map(item -> item.getPriceAtAdd() != null ? item.getPriceAtAdd() : BigDecimal.ZERO)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal frete = new BigDecimal("15.00");
		BigDecimal total = subtotal.add(frete);

		m.addAttribute("cartItems", cartItems);
		m.addAttribute("subtotal", subtotal);
		m.addAttribute("frete", frete);
		m.addAttribute("total", total);
		m.addAttribute("enderecosPerfil", enderecosSalvos);

		return "user/order";
	}

	@PostMapping("/save-order")
	public ResponseEntity<String> saveOrder(@ModelAttribute OrderRequest request, Principal p) {
		try {
			UserDtls user = getLoggedInUserDetails(p);
			orderService.saveOrder(user.getId(), request);
			return ResponseEntity.ok("Pedido salvo com sucesso");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o pedido.");
		}
	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		UserDtls userDtls = getLoggedInUserDetails(p);
		List<Order> orders = orderService.getOrdersbyUser(userDtls.getId());
		m.addAttribute("orders", orders);
		return "user/my_orders";
	}
	
	@GetMapping("/order-details/{orderId}")
	public String orderDetails(@PathVariable("orderId") String orderId, Model m, Principal p) {
	    // 1. Busca o pedido no banco de dados pelo código
	    Order order = orderService.getOrderByOrderId(orderId);
	    
	    // 2. Se o pedido não existir (alguém digitou a URL errada), manda de volta pros pedidos
	    if (order == null) {
	        return "redirect:/user/user-orders";
	    }
	    
	    // 3. Opcional, mas recomendado: Garantir que o pedido pertence ao usuário logado
	    UserDtls user = getLoggedInUserDetails(p);
	    if (!order.getUser().getId().equals(user.getId())) {
	        return "redirect:/user/user-orders"; // Impede de ver pedido dos outros
	    }

	    // 4. Manda o pedido para o HTML
	    m.addAttribute("pedido", order);
	    

	    return "user/order_details"; // Nome do seu arquivo HTML
	}

	@GetMapping("/update-status")
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
			if (updateOrderStatus != null) {
				commonUtil.sendMailForOrder(updateOrderStatus, status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrderStatus)) {
			session.setAttribute("succMsg", "Status atualizado");
		} else {
			session.setAttribute("errorMsg", "Status não atualizado");
		}

		return "redirect:/user/user-orders"; // Redireciona para os pedidos do usuário
	}

	// --- Métodos Auxiliares e Perfil ---

	private UserDtls getLoggedInUserDetails(Principal p) {
		return userService.getUserbyEmail(p.getName());
	}

	@GetMapping("/profile")
	public String profile(Model m, Principal p) {
	    UserDtls user = getLoggedInUserDetails(p);
	    
	    // Busca os endereços do usuário logado
	    List<Endereco> enderecos = enderecoRepository.findByUserId(user.getId());
	    m.addAttribute("enderecos", enderecos);
	    
	    // Manda um objeto vazio para o formulário de "Novo Endereço"
	    m.addAttribute("novoEndereco", new Endereco());
	    
	    return "user/profile";
	}
	@PostMapping("/endereco/salvar")
	// Mudamos de "Endereco endereco" para "Endereco enderecoModel" com a anotação explícita
	public String salvarEndereco(@ModelAttribute("enderecoModel") Endereco enderecoModel, Principal p, HttpSession session) {
	    UserDtls user = getLoggedInUserDetails(p);
	    
	    enderecoModel.setUser(user); 
	    enderecoRepository.save(enderecoModel);
	    
	    session.setAttribute("succMsg", "Endereço salvo com sucesso!");
	    
	    return "redirect:/user/profile#enderecos"; 
	}

	
	@GetMapping("/endereco/excluir/{id}")
	public String excluirEndereco(@PathVariable Long id, HttpSession session) {
	    // Pode adicionar uma validação aqui para garantir que o endereço pertence ao usuário logado, se quiser
	    enderecoRepository.deleteById(id);
	    session.setAttribute("succMsg", "Endereço removido com sucesso!");
	    
	    return "redirect:/user/profile#enderecos";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Perfil não atualizado");
		} else {
			session.setAttribute("succMsg", "Perfil atualizado");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);
		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getSenha());
		if (matches) {
			loggedInUserDetails.setSenha(passwordEncoder.encode(newPassword));
			userService.updateUser(loggedInUserDetails);
			session.setAttribute("succMsg", "Senha atualizada");
		} else {
			session.setAttribute("errorMsg", "Senha atual incorreta");
		}
		return "redirect:/user/profile";
	}
}