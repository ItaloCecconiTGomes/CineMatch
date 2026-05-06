package service;

import model.Usuario;

public interface NotificadorPush {
	void enviar(Usuario usuario, String mensagem);
}