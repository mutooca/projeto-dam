// services/infraestrutura-service.ts
import { authenticatedRequest } from './api-client';

export interface InfraestruturaDisponivel {
    nome: string;
    registadoNaBd: boolean;
}

export interface RegistarInfraestruturaRequest {
    emailGestor: string;
    nome: string;
    latitude: number;
    longitude: number;
    raio: number;
    capacidade: number;
    bonusEntrega: number;
    custoPost: number;
}

export interface InfraestruturaResponse {
    id: number;
    nome: string;
    latitude: string;
    longitude: string;
    raio: string;
    capacidade: number;
    premio: number;
    regras?: string;
    gps: boolean;
    wifi: boolean;
    ssids?: string;
}

// 1. Buscar infraestruturas disponíveis no UDDI
export async function buscarInfraestruturasDisponiveis(): Promise<InfraestruturaDisponivel[]> {
    return authenticatedRequest<InfraestruturaDisponivel[]>('/infraestruturas/disponiveis-uddi');
}

// 2. Registar nova infraestrutura
export async function registarInfraestrutura(
    data: RegistarInfraestruturaRequest
): Promise<InfraestruturaResponse> {
    return authenticatedRequest<InfraestruturaResponse>('/infraestruturas/registar', {
        method: 'POST',
        body: JSON.stringify(data),
    });
}

// 3. Buscar todas as infraestruturas (para listar)
export async function buscarTodasInfraestruturas(): Promise<InfraestruturaResponse[]> {
    return authenticatedRequest<InfraestruturaResponse[]>('/infraestruturas');
}