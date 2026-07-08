// middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    const ticket = request.cookies.get('ticket');
    const { pathname } = request.nextUrl;

    console.log('🔍 Middleware - Path:', pathname);
    console.log('🔍 Middleware - Ticket:', ticket?.value ? '✅ Existe' : '❌ Não existe');

    // 🗂️ Recursos estáticos
    const isStatic = pathname.startsWith('/_next') ||
        pathname.startsWith('/favicon.ico') ||
        pathname.startsWith('/public');

    // 🔓 Rotas públicas
    const publicRoutes = ['/', '/api/auth/login', '/api/auth/authenticator'];
    const isPublicRoute = publicRoutes.includes(pathname);

    if (isStatic) {
        return NextResponse.next();
    }

    if (isPublicRoute) {
        return NextResponse.next();
    }

    if (!ticket) {
        console.log('⛔ Redirecionando para login (sem ticket)');
        const url = new URL('/', request.url);
        return NextResponse.redirect(url);
    }

    console.log('✅ Ticket válido - permitindo acesso');
    return NextResponse.next();
}

export const config = {
    matcher: [
        '/((?!_next/static|_next/image|favicon.ico|public).*)',
    ],
};