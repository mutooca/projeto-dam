// middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    const ticket = request.cookies.get('ticket');
    const { pathname } = request.nextUrl;

    console.log('🔍 Middleware - Path:', pathname);
    console.log('🔍 Middleware - Ticket:', ticket ? '✅ Existe' : '❌ Não existe');

    // 🗂️ Recursos estáticos
    const isStatic = pathname.startsWith('/_next') ||
        pathname.startsWith('/favicon.ico') ||
        pathname.startsWith('/public');

    // 🔓 Rotas públicas (acesso livre)
    const publicRoutes = ['/', '/api/auth/login', '/api/auth/authenticator'];
    const isPublicRoute = publicRoutes.includes(pathname);

    // ✅ Permite recursos estáticos
    if (isStatic) {
        return NextResponse.next();
    }

    // ✅ Permite rotas públicas
    if (isPublicRoute) {
        return NextResponse.next();
    }

    // 🔐 Se NÃO tem ticket → redireciona para login
    if (!ticket) {
        console.log('⛔ Redirecionando para login (sem ticket)');
        const url = new URL('/', request.url);
        return NextResponse.redirect(url);
    }

    // ✅ Se tem ticket → permite acesso
    console.log('✅ Ticket válido - permitindo acesso');
    return NextResponse.next();
}

export const config = {
    matcher: [
        /*
         * Match all request paths except:
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico (favicon file)
         * - public folder
         */
        '/((?!_next/static|_next/image|favicon.ico|public).*)',
    ],
};