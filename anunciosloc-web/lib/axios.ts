// lib/axios.ts
import axios, { AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { authService } from '../services/authServices'; // ← CAMINHO RELATIVO
import toast from 'react-hot-toast';

// Instância do axios com configuração base
export const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
});

// Interceptor para adicionar headers de autenticação automaticamente
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig): Promise<InternalAxiosRequestConfig> => {
    // Verifica se a requisição é autenticada
    if (authService.isAuthenticated() && config.headers) {
      try {
        // Gera authenticator e adiciona headers
        const authHeaders = await authService.getAuthHeaders();
        config.headers['X-Kerberos-Ticket'] = authHeaders['X-Kerberos-Ticket'];
        config.headers['X-Kerberos-Authenticator'] = authHeaders['X-Kerberos-Authenticator'];
      } catch (error) {
        console.error('Erro ao gerar authenticator:', error);
        // Se falhar ao gerar authenticator, tenta com os headers armazenados
        try {
          const storedHeaders = authService.getStoredHeaders();
          config.headers['X-Kerberos-Ticket'] = storedHeaders['X-Kerberos-Ticket'];
        } catch (storedError) {
          console.error('Erro ao obter headers armazenados:', storedError);
        }
      }
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratar erros de autenticação
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Token expirado ou inválido
      toast.error('Sessão expirada. Faça login novamente.');
      // Limpa o estado e redireciona para login
      authService.logout();
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);