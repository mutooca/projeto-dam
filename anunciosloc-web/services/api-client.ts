// services/api-client.ts
import { getCookie } from 'cookies-next';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// 🔥 Função manual para ler cookies (fallback)
function getCookieManual(name: string): string | null {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        return parts.pop()?.split(';').shift() || null;
    }
    return null;
}

export async function authenticatedRequest<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    // 🔥 Lê todos os cookies
    let ticket = getCookie('ticket') as string;
    let email = getCookie('userEmail') as string;
    let sessionId = getCookie('sessionId') as string;

    // Fallback para leitura manual
    if (!ticket || !email || !sessionId) {
        console.log('⚠️ Cookies não encontrados com cookies-next, tentando manual...');
        ticket = getCookieManual('ticket') || '';
        email = getCookieManual('userEmail') || '';
        sessionId = getCookieManual('sessionId') || '';
    }

    console.log(`🔍 authenticatedRequest para: ${endpoint}`);
    console.log('🎫 Ticket:', ticket ? '✅ Existe' : '❌ Não existe');
    console.log('📧 Email:', email || '❌ não encontrado');
    console.log('🆔 SessionId:', sessionId || '❌ não encontrado');

    // 🔐 Verifica se todos os cookies estão presentes
    if (!ticket || !email || !sessionId) {
        console.error('❌ Sessão incompleta:', { 
            ticket: ticket ? '✅' : '❌', 
            email: email ? '✅' : '❌', 
            sessionId: sessionId ? '✅' : '❌' 
        });
        throw new Error('Sessão não encontrada. Faça login novamente.');
    }

    try {
        // 🔑 Gera um novo autenticador para esta requisição
        console.log('🔑 Gerando autenticador...');
        const authResponse = await fetch('/api/auth/authenticator', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ sessionId, email }),
        });

        if (!authResponse.ok) {
            const error = await authResponse.json().catch(() => ({}));
            console.error('❌ Falha ao gerar autenticador:', error);
            throw new Error(error.message || 'Falha ao gerar autenticador');
        }

        const { authenticator } = await authResponse.json();
        console.log('✅ Autenticador gerado com sucesso');

        // 🚀 Faz a requisição com ticket + authenticator nos headers
        console.log(`🚀 Enviando requisição para: ${API_BASE_URL}${endpoint}`);
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${ticket}`, // 🔥 Ticket no header
                'X-Authenticator': authenticator,   // 🔥 Authenticator
                'X-Session-ID': sessionId,          // 🔥 SessionId
                ...options.headers,
            },
            credentials: 'include',
        });

        console.log(`📊 Status da resposta: ${response.status}`);

        // Se não autorizado, redireciona para login
        if (response.status === 401 || response.status === 403) {
            console.warn('⛔ Não autorizado - limpando sessão');
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
            console.error(`❌ Erro ${response.status}:`, errorData);
            throw new Error(errorData.message || `Erro ${response.status} na requisição`);
        }

        const data = await response.json();
        console.log(`✅ Requisição para ${endpoint} concluída com sucesso`);
        return data;

    } catch (error) {
        console.error('❌ Erro em authenticatedRequest:', error);
        throw error;
    }
}