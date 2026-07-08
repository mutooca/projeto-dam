import { setCookie, getCookie, removeCookie } from 'cookies-next';
import { LoginRequest, LoginResponse, AuthenticatorResponse } from './types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8085/api';


export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
    credentials: 'include', // Importante para cookies
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Erro ao fazer login');
  }

  const result = await response.json();

  // Guarda ticket em cookie HTTP-only (apenas servidor)
  // Nota: No Next.js App Router, precisamos de route handler
  // Vamos usar uma abordagem híbrida

  return result;
}

// 2. Gerar autenticador antes de cada requisição
export async function generateAuthenticator(
  sessionId: string,
  email: string
): Promise<string> {
  const response = await fetch(`${API_BASE_URL}/kerberos/create-authenticator`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      sessionId,
      email,
    }),
    credentials: 'include',
  });

  if (!response.ok) {
    throw new Error('Falha ao gerar autenticador');
  }

  const data: AuthenticatorResponse = await response.json();
  return data.authenticator;
}


export async function authenticatedRequest<T>(
  endpoint: string,
  options: RequestInit = {},
  email: string
): Promise<T> {
  const sessionId = getCookie('sessionId') as string;
  
  if (!sessionId) {
    throw new Error('Sessão não encontrada. Faça login novamente.');
  }

  // Gera novo autenticador para CADA requisição
  const authenticator = await generateAuthenticator(sessionId, email);

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

  if (response.status === 401 || response.status === 403) {
    // Autenticador expirado ou inválido
    await logout();
    throw new Error('Sessão expirada. Faça login novamente.');
  }

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Erro na requisição');
  }

  return response.json();
}

// 4. Logout - limpa tudo
export async function logout(): Promise<void> {
  removeCookie('ticket');
  removeCookie('sessionId');
  removeCookie('sessionKey');
  removeCookie('userEmail');
  removeCookie('userRole');
  
  // Opcional: chamar endpoint de logout no backend
  try {
    await fetch(`${API_BASE_URL}/auth/logout`, {
      method: 'POST',
      credentials: 'include',
    });
  } catch (error) {
    console.error('Erro ao fazer logout no servidor:', error);
  }
}