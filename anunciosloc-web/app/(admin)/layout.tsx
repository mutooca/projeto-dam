// app/(admin)/layout.tsx
'use client'
import { useEffect, useState } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import { authService } from '@/services/authServices';
import SidebarAdmin from './sidebar_admin/page';

export default function AdminLayout({
    children,
}: {
    children: React.ReactNode
}) {
    const router = useRouter();
    const pathname = usePathname();
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const auth = authService.isAuthenticated();
        setIsAuthenticated(auth);
        
        // Se não estiver autenticado e não estiver na página de login
        if (!auth) {
            router.push('/login');
        }
        
        setLoading(false);
    }, [router]);

    // Enquanto verifica autenticação, mostra loading
    if (loading) {
        return (
            <div className="flex items-center justify-center h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-amber-500"></div>
            </div>
        );
    }

    // Se não estiver autenticado, não renderiza nada
    if (!isAuthenticated) {
        return null;
    }

    return (
        <div className="flex h-screen">
            <SidebarAdmin />
            <main className="flex-1 ml-0 md:ml-64 overflow-y-auto p-4">
                {children}
            </main>
        </div>
    );
}