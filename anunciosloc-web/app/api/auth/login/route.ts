// app/api/auth/login/route.ts
import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';

const API_BASE_URL = process.env.BACKEND_API_URL || 'http://localhost:8080/api';

export async function POST(request: NextRequest) {
    try {
        const body = await request.json();

        // 🔥 Rota específica para login de ADMIN
        const response = await fetch(`${API_BASE_URL}/auth/login/admin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body),
        });

        const data = await response.json();

        if (!response.ok) {
            return NextResponse.json(
                { success: false, message: data.message || 'Erro no login' },
                { status: response.status }
            );
        }

        // ✅ Se chegou aqui, o backend já validou que é ADMIN
        // (não precisamos de verificar novamente, mas mantemos por segurança)
        if (data.role && data.role !== 'ADMIN') {
            return NextResponse.json(
                { success: false, message: 'Acesso restrito a administradores' },
                { status: 403 }
            );
        }

        // Guarda cookies HTTP-only (seguros)
        const cookieStore = await cookies();

        // Ticket - HTTP-only, Secure, SameSite=Strict
        cookieStore.set('ticket', data.ticket, {
            httpOnly: true,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'strict',
            maxAge: 60 * 60 * 24, // 24 horas
            path: '/',
        });

        // SessionId - HTTP-only
        cookieStore.set('sessionId', data.sessionId, {
            httpOnly: true,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'strict',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        // SessionKey - HTTP-only
        cookieStore.set('sessionKey', data.sessionKey, {
            httpOnly: true,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'strict',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        // Email e Role - acessíveis no client (não sensíveis)
        cookieStore.set('userEmail', body.email, {
            httpOnly: false,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'strict',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        cookieStore.set('userRole', data.role || 'ADMIN', {
            httpOnly: false,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'strict',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        return NextResponse.json({
            success: true,
            message: data.message || 'Login bem-sucedido',
            // Opcional: retornar dados não sensíveis
            user: {
                email: body.email,
                role: data.role || 'ADMIN'
            }
        });

    } catch (error) {
        console.error('Erro no login:', error);
        return NextResponse.json(
            { success: false, message: 'Erro interno do servidor' },
            { status: 500 }
        );
    }
}