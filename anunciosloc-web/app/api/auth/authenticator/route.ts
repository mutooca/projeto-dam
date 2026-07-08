// app/api/auth/authenticator/route.ts
import { NextRequest, NextResponse } from 'next/server';

// 🔥 KERBEROS na porta 8085
const KERBEROS_API_URL = process.env.KERBEROS_API_URL || 'http://localhost:8085/api';

export async function POST(request: NextRequest) {
    try {
        const { sessionId, email } = await request.json();

        console.log('🔑 Gerando autenticador para:', email);
        console.log('🆔 SessionId:', sessionId);
        console.log('🌐 Kerberos URL:', `${KERBEROS_API_URL}/kerberos/create-authenticator`);

        if (!sessionId || !email) {
            return NextResponse.json(
                { error: 'sessionId e email são obrigatórios' },
                { status: 400 }
            );
        }

        // 🔥 Chama o KERBEROS na porta 8085
        const response = await fetch(`${KERBEROS_API_URL}/kerberos/create-authenticator`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ sessionId, email }),
        });

        console.log('📊 Status do Kerberos:', response.status);

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            console.error('❌ Erro no Kerberos:', errorData);
            return NextResponse.json(
                { error: errorData.message || 'Falha ao gerar autenticador' },
                { status: response.status }
            );
        }

        const data = await response.json();
        console.log('✅ Autenticador gerado com sucesso');
        return NextResponse.json({ authenticator: data.authenticator });

    } catch (error) {
        console.error('❌ Erro ao gerar autenticador:', error);
        return NextResponse.json(
            { error: 'Erro interno do servidor' },
            { status: 500 }
        );
    }
}