const API_BASE_URL = 'http://localhost:8080/api';

export interface LoginResponse {
  success: boolean;
  ticket: string;
  sessionId: string;
  sessionKey: string;
  message: string;
}

export interface LoginRequest {
  email: string;
  palavraChave: string;
}

export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Erro ao fazer login');
  }

  const result = await response.json();
  return result;
}