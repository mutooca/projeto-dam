// services/authService.ts
import axios, { AxiosResponse } from 'axios';

// Configuração base da API
const API_BASE_URL = 'http://localhost:8080/api';
const KERBEROS_API_URL = 'http://localhost:8085/api/kerberos';

// Interfaces
interface LoginPayload {
  email: string;
  palavraChave: string;
}

interface LoginResponse {
  success: boolean;
  ticket: string;
  sessionId: string;
  sessionKey: string;
  message: string;
}

interface AuthenticatorResponse {
  authenticator: string;
}

interface AuthState {
  ticket: string;
  sessionId: string;
  email: string;
}

// Estado da autenticação em memória
let authState: AuthState = {
  ticket: '',
  sessionId: '',
  email: '',
};

export const authService = {
  // Login - obtém ticket e sessionId
  async login(email: string, password: string): Promise<LoginResponse> {
    const payload: LoginPayload = {
      email,
      palavraChave: password,
    };

    const response: AxiosResponse<LoginResponse> = await axios.post(
      `${API_BASE_URL}/auth/login`,
      payload,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    // Guarda em memória (não no localStorage)
    if (response.data.success) {
      authState.ticket = response.data.ticket;
      authState.sessionId = response.data.sessionId;
      authState.email = email;
    }

    return response.data;
  },

  // Gera authenticator para uma requisição específica
  async generateAuthenticator(): Promise<string> {
    if (!authState.sessionId || !authState.email) {
      throw new Error('Sessão não iniciada. Faça login primeiro.');
    }

    const response: AxiosResponse<AuthenticatorResponse> = await axios.post(
      `${KERBEROS_API_URL}/create-authenticator`,
      {
        sessionId: authState.sessionId,
        email: authState.email,
      },
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    return response.data.authenticator;
  },

  // Obtém headers para requisições autenticadas
  async getAuthHeaders(): Promise<Record<string, string>> {
    if (!authState.ticket || !authState.sessionId) {
      throw new Error('Usuário não autenticado');
    }

    // Gera um novo authenticator para cada requisição (expira em 5 min)
    const authenticator = await this.generateAuthenticator();

    return {
      'X-Kerberos-Ticket': authState.ticket,
      'X-Kerberos-Authenticator': authenticator,
      'Content-Type': 'application/json',
    };
  },

  // Obtém os headers sem gerar authenticator (para casos específicos)
  getStoredHeaders(): Record<string, string> {
    if (!authState.ticket) {
      throw new Error('Usuário não autenticado');
    }

    return {
      'X-Kerberos-Ticket': authState.ticket,
      'Content-Type': 'application/json',
    };
  },

  // Verifica se o usuário está autenticado
  isAuthenticated(): boolean {
    return !!authState.ticket && !!authState.sessionId;
  },

  // Obtém o email do usuário autenticado
  getCurrentUser(): string | null {
    return authState.email || null;
  },

  // Faz logout - limpa o estado em memória
  logout(): void {
    authState = {
      ticket: '',
      sessionId: '',
      email: '',
    };
  },

  // Método para fazer requisições autenticadas
  async authenticatedRequest<T>(
    method: 'GET' | 'POST' | 'PUT' | 'DELETE',
    url: string,
    data?: any
  ): Promise<T> {
    const headers = await this.getAuthHeaders();

    const config = {
      method,
      url,
      headers,
      data,
    };

    const response = await axios(config);
    return response.data;
  },
};