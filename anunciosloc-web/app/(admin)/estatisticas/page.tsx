import GraficoInfraestuturas from "@/app/components/grafico/GraficoInfraestruturas";
import GraficoCobertura from "@/app/components/grafico/GraficoCobertura";


export default function Estatisticas() {
return (
    <div className="max-w-5xl w-full space-y-6 mt-28 px-4">
        <div className="font-semibold text-4xl">Estatísticas do Sistema</div>
        <div className="text-gray-600 font-semibold">Indicadores de uso e desempenho da rede AnunciosLoc.</div>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <GraficoInfraestuturas/>
           <GraficoCobertura/>
        </div>
    </div>
);
}