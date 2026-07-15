// services/authService.ts
import axios, { AxiosResponse } from 'axios';

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

export interface AdminInfo {
    nome: string;
    email: string;
    role: string;
}

// Estado da autenticação em memória
let authState: AuthState = {
    ticket: '',
    sessionId: '',
    email: '',
};

// Funções auxiliares para cookies
function setCookie(name: string, value: string, days: number = 1) {
    if (typeof document === 'undefined') return;
    const expires = new Date();
    expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
    document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/`;
}

function getCookie(name: string): string | null {
    if (typeof document === 'undefined') return null;
    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
        const [key, value] = cookie.trim().split('=');
        if (key === name) return value;
    }
    return null;
}

function deleteCookie(name: string) {
    if (typeof document === 'undefined') return;
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/`;
}

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

        if (response.data.success) {
            authState.ticket = response.data.ticket;
            authState.sessionId = response.data.sessionId;
            authState.email = email;
            
            // Guarda no cookie para o middleware
            setCookie('ticket', response.data.ticket);
            setCookie('email', email);
        }

        return response.data;
    },

    // Gera authenticator
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

        const authenticator = await this.generateAuthenticator();

        return {
            'X-Kerberos-Ticket': authState.ticket,
            'X-Kerberos-Authenticator': authenticator,
            'Content-Type': 'application/json',
        };
    },

    // Obtém os headers armazenados
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
        const ticket = getCookie('ticket');
        return !!authState.ticket || !!ticket;
    },

    // Obtém o email do usuário autenticado
    getCurrentUser(): string | null {
        return authState.email || getCookie('email') || null;
    },

    // Busca informações do admin
    async getAdminInfo(email: string): Promise<AdminInfo> {
        const response = await axios.get<AdminInfo>(
            `${API_BASE_URL}/admin/me?email=${encodeURIComponent(email)}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );
        return response.data;
    },

    // Faz logout
    logout(): void {
        authState = {
            ticket: '',
            sessionId: '',
            email: '',
        };
        deleteCookie('ticket');
        deleteCookie('email');
    },
};