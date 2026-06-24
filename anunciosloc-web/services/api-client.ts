// services/api-client.ts
import { getCookie } from 'cookies-next';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export async function authenticatedRequest<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const email = getCookie('userEmail') as string;
    const sessionId = getCookie('sessionId') as string;

    if (!email || !sessionId) {
        throw new Error('Sessão não encontrada. Faça login novamente.');
    }

    // Gera um novo autenticador para esta requisição
    const authResponse = await fetch('/api/auth/authenticator', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ sessionId, email }),
    });

    if (!authResponse.ok) {
        const error = await authResponse.json().catch(() => ({}));
        throw new Error(error.message || 'Falha ao gerar autenticador');
    }

    const { authenticator } = await authResponse.json();

    // Faz a requisição com o autenticador
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            'X-Authenticator': authenticator,
            'X-Session-ID': sessionId,
            ...options.headers,
        },
        credentials: 'include',
    });

    // Se não autorizado, redireciona para login
    if (response.status === 401 || response.status === 403) {
        // Limpa cookies no client
        document.cookie.split(';').forEach(cookie => {
            document.cookie = cookie
                .replace(/^ +/, '')
                .replace(/=.*/, `=; expires=${new Date(0).toUTCString()}; path=/`);
        });
        
        window.location.href = '/';
        throw new Error('Sessão expirada. Faça login novamente.');
    }

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Erro ${response.status} na requisição`);
    }

    return response.json();
}