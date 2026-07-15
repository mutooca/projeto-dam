// middleware.ts (na raiz do projeto)
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

// Rotas públicas (apenas login)
const publicRoutes = ['/login'];

export function middleware(request: NextRequest) {
    const path = request.nextUrl.pathname;

    // Verifica se a rota é pública (login)
    const isPublicRoute = publicRoutes.some(route => path === route);

    // Verifica se o usuário está autenticado via cookie
    const ticket = request.cookies.get('ticket')?.value;
    const isAuthenticated = !!ticket;

    // Se for a rota de login e já estiver autenticado, redireciona para dashboard
    if (path === '/login' && isAuthenticated) {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    // Se for uma rota pública (login), permite acesso
    if (isPublicRoute) {
        return NextResponse.next();
    }

    // Se não estiver autenticado e tentar acessar qualquer outra rota
    if (!isAuthenticated) {
        // Redireciona para login
        const loginUrl = new URL('/login', request.url);
        return NextResponse.redirect(loginUrl);
    }

    // Se estiver autenticado, permite acesso
    return NextResponse.next();
}

export const config = {
    // Matcher para todas as rotas, exceto arquivos estáticos e API
    matcher: [
        /*
         * Match all request paths except for the ones starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico (favicon file)
         * - public folder
         */
        '/((?!api|_next/static|_next/image|favicon.ico|.*\\.png$|.*\\.jpg$|.*\\.svg$|.*\\.css$).*)',
    ],
};