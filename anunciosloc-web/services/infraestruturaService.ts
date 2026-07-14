// services/infraestruturaService.ts (completo)

import axios from 'axios';
import { authService } from './authServices';

const API_BASE_URL = 'http://localhost:8080/api';

// Interface para os dados do Dashboard
export interface DashboardData {
    totalUtilizadores: number;
    utilizadoresAtivos: number;
    utilizadoresInativos: number;
    totalInfraestruturas: number;
    infraestruturasOnline: number;
    infraestruturasOffline: number;
    totalLocais: number;
    totalAnuncios: number;
    totalEntregas: number;
    totalConexoes: number;
    ultimaAtualizacao: string;
}

// DTO exato da API
export interface InfraestruturaAdminDTO {
    id: string;
    nome: string;
    url: string;
    latitude?: number | null;
    longitude?: number | null;
    raio?: number | null;
    capacidade: number;
    bonusEntrega: number;
    custoPost?: number | null;
    ativa?: boolean;
    online?: boolean;
    registadaUDDI?: boolean;
    conexoesAtuais?: number;
}

export interface InfraestruturaNaoRegistada {
    nome: string;
    url: string;
    online: boolean;
    registadaUDDI: boolean;
    ativa: boolean;
}

export interface RegistarInfraestruturaRequest {
    nome: string;
    url: string;
    latitude?: number;
    longitude?: number;
    raio?: number;
    capacidade: number;
    bonusEntrega: number;
    custoPost?: number;
    restricoes?: string;
    ssid?: string;
    nome_wifi?: string;
}

export interface RegistarInfraestruturaResponse {
    id?: string;
    nome?: string;
    url?: string;
    latitude?: number;
    longitude?: number;
    raio?: number;
    capacidade?: number;
    bonusEntrega?: number;
    custoPost?: number;
    ativa?: boolean;
    dataRegisto?: string;
    restricoes?: string;
    mensagem?: string;
    success?: boolean;
    message?: string;
}

export interface RedimensionarInfraRequest {
    capacidade: number;
    bonusEntrega: number;
    custoPost: number;
    raio: number;
}

export interface RedimensionarInfraResponse {
    success?: boolean;
    message?: string;
    mensagem?: string;
    id?: string;
    capacidade?: number;
    bonusEntrega?: number;
    custoPost?: number;
    raio?: number;
}

export interface RecolocarInfraRequest {
    latitude: number;
    longitude: number;
    raio: number;
}

export interface RecolocarInfraResponse {
    success?: boolean;
    message?: string;
    mensagem?: string;
    id?: string;
    latitude?: number;
    longitude?: number;
    raio?: number;
}

export const infraestruturaService = {
    // Busca dados do Dashboard (PÚBLICO - sem autenticação)
    async buscarDashboard(): Promise<DashboardData> {
        const response = await axios.get<DashboardData>(
            `${API_BASE_URL}/admin/dashboard`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );
        return response.data;
    },

    // Busca todas as infraestruturas (PÚBLICO - sem autenticação)
    async buscarTodas(): Promise<InfraestruturaAdminDTO[]> {
        const response = await axios.get<InfraestruturaAdminDTO[]>(
            `${API_BASE_URL}/admin/infraestruturas`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );
        return response.data;
    },

    // Busca infraestruturas não registadas (PÚBLICO - sem autenticação)
    async buscarNaoRegistadas(): Promise<InfraestruturaNaoRegistada[]> {
        const response = await axios.get<InfraestruturaNaoRegistada[]>(
            `${API_BASE_URL}/admin/infraestruturas/nao-registadas`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );
        return response.data;
    },

    // Registra uma nova infraestrutura (AUTENTICADO - com Kerberos)
    async registarInfraestrutura(data: RegistarInfraestruturaRequest): Promise<RegistarInfraestruturaResponse> {
        if (!authService.isAuthenticated()) {
            throw new Error('Usuário não autenticado. Faça login primeiro.');
        }

        const headers = await authService.getAuthHeaders();

        console.log('Headers enviados:', headers);
        console.log('Dados enviados para o backend:', JSON.stringify(data, null, 2));

        try {
            const response = await axios.post<RegistarInfraestruturaResponse>(
                `${API_BASE_URL}/admin/infraestruturas/registar`,
                data,
                {
                    headers: {
                        ...headers,
                        'Content-Type': 'application/json',
                    },
                }
            );
            console.log('Resposta do backend:', response.data);
            return response.data;
        } catch (error: any) {
            if (error.response) {
                console.error('Status:', error.response.status);
                console.error('Dados do erro:', error.response.data);
                
                const errorMessage = error.response.data?.mensagem || 
                                   error.response.data?.message || 
                                   JSON.stringify(error.response.data);
                throw new Error(`Erro ${error.response.status}: ${errorMessage}`);
            }
            throw error;
        }
    },

    // Redimensiona uma infraestrutura (AUTENTICADO - com Kerberos)
    async redimensionarInfraestrutura(
        id: string, 
        data: RedimensionarInfraRequest
    ): Promise<RedimensionarInfraResponse> {
        if (!authService.isAuthenticated()) {
            throw new Error('Usuário não autenticado. Faça login primeiro.');
        }

        if (!id) {
            throw new Error('ID da infraestrutura é obrigatório.');
        }

        const headers = await authService.getAuthHeaders();

        console.log('Headers enviados:', headers);
        console.log('ID da infraestrutura:', id);
        console.log('Dados de redimensionamento:', JSON.stringify(data, null, 2));

        try {
            const response = await axios.put<RedimensionarInfraResponse>(
                `${API_BASE_URL}/admin/infraestruturas/${id}/redimensionar`,
                data,
                {
                    headers: {
                        ...headers,
                        'Content-Type': 'application/json',
                    },
                }
            );
            console.log('Resposta do backend:', response.data);
            return response.data;
        } catch (error: any) {
            if (error.response) {
                console.error('Status:', error.response.status);
                console.error('Dados do erro:', error.response.data);
                
                const errorMessage = error.response.data?.mensagem || 
                                   error.response.data?.message || 
                                   JSON.stringify(error.response.data);
                throw new Error(`Erro ${error.response.status}: ${errorMessage}`);
            }
            throw error;
        }
    },

    // Recoloca uma infraestrutura (AUTENTICADO - com Kerberos)
    async recolocarInfraestrutura(
        id: string, 
        data: RecolocarInfraRequest
    ): Promise<RecolocarInfraResponse> {
        if (!authService.isAuthenticated()) {
            throw new Error('Usuário não autenticado. Faça login primeiro.');
        }

        if (!id) {
            throw new Error('ID da infraestrutura é obrigatório.');
        }

        const headers = await authService.getAuthHeaders();

        console.log('Headers enviados:', headers);
        console.log('ID da infraestrutura:', id);
        console.log('Dados de recolocação:', JSON.stringify(data, null, 2));

        try {
            const response = await axios.put<RecolocarInfraResponse>(
                `${API_BASE_URL}/admin/infraestruturas/${id}/recolocar`,
                data,
                {
                    headers: {
                        ...headers,
                        'Content-Type': 'application/json',
                    },
                }
            );
            console.log('Resposta do backend:', response.data);
            return response.data;
        } catch (error: any) {
            if (error.response) {
                console.error('Status:', error.response.status);
                console.error('Dados do erro:', error.response.data);
                
                const errorMessage = error.response.data?.mensagem || 
                                   error.response.data?.message || 
                                   JSON.stringify(error.response.data);
                throw new Error(`Erro ${error.response.status}: ${errorMessage}`);
            }
            throw error;
        }
    },
};