export function getUsername() {
  // Retorna o nome do usuário salvo no localStorage
  return localStorage.getItem('username') || 'Visitante';
}

export function logout() {
  // Remove dados do usuário do localStorage
  localStorage.removeItem('username');
  // Você pode remover outros dados de autenticação aqui
}
