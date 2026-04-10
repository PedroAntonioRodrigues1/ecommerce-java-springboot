package com.retalho.vendas.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.retalho.vendas.model.Order;
import com.retalho.vendas.model.OrderAdress;
import com.retalho.vendas.model.ProdutoOrder;
import com.retalho.vendas.model.UserDtls;
import com.retalho.vendas.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserService userService;

	public Boolean sendMail(String url, String recipientEmail) throws UnsupportedEncodingException, MessagingException {

		// Cria um objeto de e-mail vazio no formato MIME (permite HTML, anexos, etc.)
		MimeMessage message = mailSender.createMimeMessage();

		// Classe auxiliar que facilita configurar remetente, destinatário, assunto e
		// corpo
		MimeMessageHelper helper = new MimeMessageHelper(message);

		// Define o remetente do e-mail:
		// 1º parâmetro = e-mail real que envia
		// 2º parâmetro = nome amigável que aparece para quem recebe
		helper.setFrom("p3dro.rodrigues07@gmail.com", "Varanda dos Retalhos");

		// Define o destinatário (quem vai receber o e-mail)
		helper.setTo(recipientEmail);

		// Monta o corpo do e-mail em HTML (com título, parágrafos e botão estilizado)
		String content = "" + "<!DOCTYPE html>" + "<html>"
				+ "<body style='font-family: Arial, sans-serif; line-height:1.6;'>" + "  <h3>Olá!</h3>"
				+ "  <p>Você solicitou a redefinição da sua senha.</p>"
				+ "  <p>Para continuar, clique no link abaixo:</p>" + "  <p><a href='" + url + "' "
				+ "        style='background-color:#4CAF50; color:white; padding:10px 15px; "
				+ "               text-decoration:none; border-radius:5px;'>" + "        Redefinir Senha</a></p>"
				+ "  <p>Se você não fez essa solicitação, ignore este e‑mail.</p>" + "  <br>"
				+ "  <p>Atenciosamente,<br><strong>Equipe Varanda dos Retalhos</strong></p>" + "</body>" + "</html>";

		// Define o assunto do e-mail (linha que aparece na caixa de entrada)
		helper.setSubject("Redefinir Senha");

		// Define o corpo do e-mail:
		// 1º parâmetro = conteúdo
		// 2º parâmetro = true → indica que o conteúdo é HTML
		helper.setText(content, true);

		// Envia a mensagem de fato usando o servidor SMTP configurado no Spring
		mailSender.send(message);

		// Retorna true indicando que o envio foi bem-sucedido
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");

	}

	public Boolean sendMailForOrder(Order order, String status)
	        throws UnsupportedEncodingException, MessagingException {

	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	    helper.setFrom("p3dro.rodrigues07@gmail.com", "Varanda dos Retalhos");
	    helper.setTo(order.getAdress().getEmail());

	    // --- 1. Montar a tabela de itens dinamicamente ---
	    StringBuilder itemsHtml = new StringBuilder();
	    BigDecimal totalOrder = BigDecimal.ZERO;

	    for (ProdutoOrder item : order.getItems()) {
	        String titulo = (item.getProduto() != null) ? item.getProduto().getTitulo() : "Produto Indisponível";
	        BigDecimal preco = item.getPriceSnapshot();
	        totalOrder = totalOrder.add(preco);

	        itemsHtml.append("<tr>")
	                 .append("<td style='padding:10px 8px 6px 0; color:#222; vertical-align:top;'>").append(titulo).append("</td>")
	                 .append("<td style='padding:10px 8px; text-align:center; color:#222; vertical-align:top;'>1</td>")
	                 .append("<td style='padding:10px 0 6px 8px; text-align:right; color:#222; vertical-align:top;'>R$ ").append(String.format("%.2f", preco)).append("</td>")
	                 .append("</tr>");
	    }

	    String htmlTemplate = """
	            <!DOCTYPE html>
	            <html>
	            <body style='font-family: Arial, sans-serif; line-height:1.5; color:#222; margin:0; padding:20px; background:#f2f4f6;'>
	              <div style='max-width:640px; margin:0 auto; background:#ffffff; padding:20px; border-radius:8px; box-shadow:0 2px 6px rgba(0,0,0,0.06);'>
	                <h2 style='color:#111; margin-top:0; font-size:20px;'>Atualização do seu pedido</h2>

	                <p style='color:#333; margin-bottom:10px;'>
	                  Olá <strong>{{customerName}}</strong>,
	                </p>

	                <p style='color:#333; margin-bottom:6px; font-weight:600;'>
	                  Status atual do pedido
	                </p>

	                <p style='text-align:center; margin:12px 0;'>
	                  <span style='display:inline-block; background-color:#0066cc; color:#fff; padding:10px 16px; border-radius:6px; font-weight:700;'>
	                    {{orderStatus}}
	                  </span>
	                </p>

	                <p style='color:#555; font-size:13px; text-align:center; margin-top:6px;'>
	                  Recebemos o pagamento e já iniciamos o processamento. Você será avisado por e‑mail quando houver um novo status.
	                </p>

	                <hr style='border:none; border-top:1px solid #e9ecef; margin:18px 0;'>

	                <h4 style='margin:0 0 8px 0; color:#111;'>Resumo do pedido</h4>

	                <table style='width:100%; border-collapse:collapse; margin-top:8px;'>
	                  <thead>
	                    <tr>
	                      <th style='text-align:left; padding:8px; border-bottom:1px solid #e9ecef; color:#444;'>Item</th>
	                      <th style='text-align:center; padding:8px; border-bottom:1px solid #e9ecef; color:#444;'>Qtd</th>
	                      <th style='text-align:right; padding:8px; border-bottom:1px solid #e9ecef; color:#444;'>Preço</th>
	                    </tr>
	                  </thead>
	                  <tbody>
	                    {{itemsTable}}
	                  </tbody>
	                  <tfoot style='border-top:1px solid #e9ecef;'>
	                    <tr>
	                      <td style='padding-top:12px; color:#666;'>Data do pedido</td>
	                      <td></td>
	                      <td style='padding-top:12px; text-align:right; color:#666;'>{{orderDate}}</td>
	                    </tr>
	                    <tr>
	                      <td style='padding-top:6px; color:#000; font-weight:700;'>Total</td>
	                      <td></td>
	                      <td style='padding-top:6px; text-align:right; color:#000; font-weight:700;'>{{totalPrice}}</td>
	                    </tr>
	                   </tfoot>
	                </table>

	                <div style='margin-top:16px; padding:12px; background:#f8f9fa; border-radius:6px;'>
	                  <p style='margin:0 0 6px 0; color:#333; font-weight:700;'>Informações do pedido</p>
	                  <p style='margin:0; color:#555; font-size:14px;'>Pedido Nº: <strong>{{orderId}}</strong></p>
	                  <p style='margin:6px 0 0 0; color:#555; font-size:14px;'>Pagamento: <strong>{{paymentMethod}}</strong></p>
	                </div>

	                <div style='margin-top:14px;'>
	                  <p style='margin:0 0 6px 0; color:#333; font-weight:700;'>Endereço de entrega</p>
	                  <p style='margin:0; color:#555; font-size:14px;'>{{deliveryAddress}}</p>
	                </div>

	                <p style='color:#555; font-size:13px; margin-top:16px;'>
	                  Para acompanhar o pedido ou tirar dúvidas, responda este e‑mail ou acesse sua conta.
	                </p>

	                <p style='margin-top:14px; color:#333;'>Atenciosamente,<br><strong>Equipe Varanda dos Retalhos</strong></p>
	              </div>
	            </body>
	            </html>
	            """;

	    // --- 2. Formatar Datas e Valores ---
	    // Como sua Model Order usa Instant, formatamos assim:
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());
	    String orderDateFormatted = dtf.format(order.getOrderDate());
	    
	    String totalPriceFormatted = String.format("R$ %.2f", totalOrder);
	    
	    OrderAdress adr = order.getAdress();
	    String deliveryAddress = String.format("%s, %s", adr.getEndereco(), adr.getNumero());
	    if (adr.getComplemento() != null && !adr.getComplemento().isEmpty()) {
	        deliveryAddress += " - " + adr.getComplemento();
	    }
	    deliveryAddress += String.format("<br>%s, %s - %s<br>CEP: %s", adr.getBairro(), adr.getCidade(),
	            adr.getEstado(), adr.getCep());

	    // --- 3. Substituição Final ---
	    String msg = htmlTemplate.replace("{{customerName}}", order.getAdress().getNomeCompleto())
	            .replace("{{orderStatus}}", status)
	            .replace("{{itemsTable}}", itemsHtml.toString()) // Insere as linhas da tabela aqui
	            .replace("{{orderDate}}", orderDateFormatted)
	            .replace("{{totalPrice}}", totalPriceFormatted)
	            .replace("{{orderId}}", order.getOrderId())
	            .replace("{{paymentMethod}}", order.getPaymentType())
	            .replace("{{deliveryAddress}}", deliveryAddress);

	    helper.setSubject("Atualização de Status - Pedido " + order.getOrderId());
	    helper.setText(msg, true);
	    
	    mailSender.send(message);

	    return true;
	}
	public UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserbyEmail(email);

		return userDtls;
	}
}
