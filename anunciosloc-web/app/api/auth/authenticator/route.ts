// app/api/auth/authenticator/route.ts
import { NextRequest, NextResponse } from 'next/server';

const API_BASE_URL = process.env.BACKEND_API_URL || 'http://localhost:8080/api';

export async function POST(request: NextRequest) {
    try {
        const { sessionId, email } = await request.json();

        if (!sessionId || !email) {
            return NextResponse.json(
                { error: 'sessionId e email são obrigatórios' },
                { status: 400 }
            );
        }

        // Chama o backend para gerar autenticador
        const response = await fetch(`${API_BASE_URL}/kerberos/create-authenticator`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ sessionId, email }),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            return NextResponse.json(
                { error: errorData.message || 'Falha ao gerar autenticador' },
                { status: response.status }
            );
        }

        const data = await response.json();
        return NextResponse.json({ authenticator: data.authenticator });

    } catch (error) {
        console.error('Erro ao gerar autenticador:', error);
        return NextResponse.json(
            { error: 'Erro interno do servidor' },
            { status: 500 }
        );
    }
}