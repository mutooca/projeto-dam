// app/api/auth/login/route.ts
import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';

const API_BASE_URL = process.env.BACKEND_API_URL || 'http://localhost:8080/api';

export async function POST(request: NextRequest) {
    try {
        const body = await request.json();

        console.log('📨 Login request:', { email: body.email });

        const response = await fetch(`${API_BASE_URL}/auth/login/admin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body),
        });

        const data = await response.json();

        if (!response.ok) {
            console.error('❌ Erro no login (backend):', data);
            return NextResponse.json(
                { success: false, message: data.message || 'Erro no login' },
                { status: response.status }
            );
        }

        if (data.role && data.role !== 'ADMIN') {
            console.warn('⛔ Tentativa de login com role não-ADMIN:', data.role);
            return NextResponse.json(
                { success: false, message: 'Acesso restrito a administradores' },
                { status: 403 }
            );
        }

        const cookieStore = await cookies();

        // 🔐 Ticket - APENAS SERVIDOR (httpOnly)
        cookieStore.set('ticket', data.ticket, {
            httpOnly: true,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'lax',
            maxAge: 60 * 60 * 24, // 24 horas
            path: '/',
        });

        // 🔓 SessionId - ACESSÍVEL NO CLIENT (necessário para o api-client)
        cookieStore.set('sessionId', data.sessionId, {
            httpOnly: false,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'lax',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        // 🔐 SessionKey - APENAS SERVIDOR
        cookieStore.set('sessionKey', data.sessionKey, {
            httpOnly: true,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'lax',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        // 📧 Email - ACESSÍVEL NO CLIENT
        cookieStore.set('userEmail', body.email, {
            httpOnly: false,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'lax',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        // 👤 Role - ACESSÍVEL NO CLIENT
        cookieStore.set('userRole', data.role || 'ADMIN', {
            httpOnly: false,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'lax',
            maxAge: 60 * 60 * 24,
            path: '/',
        });

        console.log('✅ Login bem-sucedido para:', body.email);
        console.log('🍪 Cookies guardados:', {
            ticket: data.ticket ? '✅' : '❌',
            sessionId: data.sessionId ? '✅' : '❌',
            sessionKey: data.sessionKey ? '✅' : '❌',
            userEmail: body.email,
        });

        return NextResponse.json({
            success: true,
            message: data.message || 'Login bem-sucedido',
            user: {
                email: body.email,
                role: data.role || 'ADMIN'
            }
        });

    } catch (error) {
        console.error('❌ Erro no login:', error);
        return NextResponse.json(
            { success: false, message: 'Erro interno do servidor' },
            { status: 500 }
        );
    }
}