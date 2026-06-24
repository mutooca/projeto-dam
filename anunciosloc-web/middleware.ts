// middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const ticket = request.cookies.get('ticket');
  const isLoginPage = request.nextUrl.pathname === '/';
  const isApiRoute = request.nextUrl.pathname.startsWith('/api');

  // Permite acesso à API e recursos públicos
  if (isApiRoute) {
    return NextResponse.next();
  }

  // Se não tem ticket e não está na página de login
  if (!ticket && !isLoginPage) {
    return NextResponse.redirect(new URL('/', request.url));
  }

  // Se tem ticket e está na página de login, redireciona para dashboard
  if (ticket && isLoginPage) {
    return NextResponse.redirect(new URL('/dashboard', request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    '/((?!_next/static|_next/image|favicon.ico|public).*)',
  ],
};