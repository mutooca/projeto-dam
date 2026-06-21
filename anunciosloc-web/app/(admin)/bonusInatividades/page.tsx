import { HiOutlineExclamation } from "react-icons/hi";
import { LuAward} from "react-icons/lu";
import SaldoAtuais from "@/app/components/saldosAtuais/page";

export default function BonusInatividade(){
    const qtdQuantidadeAdecremento = 8;
    const qtdAdecremento =[
        {nome:"Mara Dalva", diasInativos:"há 8d"},
        {nome:"Maria Cardoso", diasInativos:"há 10d"},
        {nome:"Pedro Mendes", diasInativos:"há 20d"},
        {nome:"Ana Bumba", diasInativos:"há 44d"}

    ]

    const utilizadores = [
        {id:1, nome:"Mara Dalva", saldo:120},
        {id: 2,nome: "João Silva", saldo: 15}
    ]

    return(
        <div className="flex flex-col  mt-28 gap-6">
            <div className="flex flex-col gap-1">
                <h2 className="text-2xl md:text-4xl font-semibold">Bónus & Inatividade</h2>
                <p className="text-sm text-gray-600 font-semibold">Atribua pontos a utilizadores e execute a política semanal de decremento.</p>
            </div>
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 max-w-5xl w-ful p-2">
                <div className="flex flex-col rounded-lg shadow p-4 gap-4 border border-[#F8960D]/30 bg-[#F8960D]/5">
                    <div className="flex flex-col space-y-1">
                        <div className="flex items-center gap-2">
                            <LuAward size={20} className="text-[#F8960D]"/>
                            <h3 className="text-sm md:text-xl font-semibold ">Atribuir Bónus</h3>
                        </div>
                     <p className="text-xs md:text-sm text-gray-600 font-semibold ">Acrescentar (ou debitar, com valor negativo) pontos ao saldo de um utilizador.</p>
                    </div>
                    <form action="" className="space-y-4">
                        <div className="flex flex-col space-y-1">
                            <label htmlFor="" className="text-sm font-semibold">Utilizador</label>
                            <select name="" id="" className="w-full p-2 rounded-lg border border-gray-200 shadow-sm outline-none focus:border-amber-500 cursor-pointer">
                                <option value="">Selecionar...</option>
                                { utilizadores.map(user =>(
                                    <option className="hover:bg-[#F8960D]/20 p-2 rounded-lg" key={user.id} value={user.id}>{user.nome}-Saldo atual{user.saldo} pts</option>))}
                            </select>

                        </div>
                        <div className="flex flex-col space-y-1">
                            <label htmlFor="" className="text-sm font-semibold">Quantidades(pts)</label>
                            <input type="number" className="w-full p-2 shadow-sm rounded-lg border border-gray-200 outline-none focus:border-amber-500" />
                        </div>
                        <button type="submit" className="w-full bg-[#F8960D] p-2 text-white rounded-lg cursor-pointer hover:bg-amber-500">⚡ Atribuir</button>
                    </form>
                </div>


                <div className="flex flex-col gap-4 p-4 rounded-lg shadow border border-gray-200">
                    <div className="flex flex-col gap-1">
                        <div className="flex items-center gap-2">
                            <HiOutlineExclamation size={20}  className="text-red-600"/>
                            <h2 className="text-sm md:text-xl font-semibold">Política de inatividade</h2>
                        </div>
                        <p className="text-xs md:text-sm font-semibold  text-gray-600">Utilizadores sem publicar há 7 dias perdem 1 pt por dia adicional.</p>
                    </div>

                    <div className="p-4 rounded-lg border border-gray-200 bg-gray-50 flex flex-col space-y-1">
                        <h2 className="text-gray-600 font-semibold ">
                            CANDIDATOS A DECREMENTO
                        </h2>
                        <span className="font-semibold text-red-600 text-4xl">{qtdQuantidadeAdecremento}</span>
                    </div>
                    {
                        qtdAdecremento.map((item, index)=>(
                            <div key={index} className="p-2 rounded-lg border border-gray-200 flex items-center justify-between">
                                <span className="">{item.nome}</span>
                                <button className="rounded-xl px-4 py-1 border border-gray-200 text-red-600 font-semibold text-xs">{item.diasInativos}</button>
                            </div>
                        ))
                    }
                    <button type="submit" className="bg-red-600 rounded-lg shadow p-2 w-full text-white font-semibold cursor-pointer hover:bg-red-500">Executar decremento semanal</button>
                </div>
            </div>
           <SaldoAtuais/> 
        </div>
    )
}