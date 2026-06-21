'use client'
import {useForm} from 'react-hook-form';
import {z} from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useRouter } from 'next/navigation';
import { BsBroadcast } from "react-icons/bs";
import toast from 'react-hot-toast';
import Link from 'next/link';
import { LuMail, LuLock} from "react-icons/lu";

const sanitizeEmail = (value:string)=>{
        if(!value) return " ";
       return value.trim().toLowerCase().replace(/\s+/g, '').replace(/[<>'"]/g, '').slice(0, 70);
}

const loginShema = z.object({
    
     email: z.string().min(1, 'O e-mail é obrigatório').transform(sanitizeEmail).refine(val => val.length > 0, 'O e-mail não pode estar vazio')
    .pipe(z.string().max(80, 'O e-mail é demasiado longo').regex(/^[a-z0-9._+-]+@[a-z0-9.-]+\.[a-z]{2,}$/, 'Formato de e-mail inválido')
    .refine(val => !val.includes('..'), 'E-mail não pode conter pontos consecutivos').refine(val => val.split('@')[0].length <= 64, 'A parte local do e-mail é demasiado longa')),
    
     password:z.string().min(1, 'A palavra-passe é obrigatória').min(6, 'A palavra-passe deve ter no mínimo 8 caracteres').max(64, 'A palavra-passe é demasiado longa')
    .refine(val => !val.match(/(.)\1{2,}/), 'A palavra-passe não pode ter caracteres repetidos consecutivamente')
    .refine(val => !['12345678', 'password', 'senha123', 'admin123', 'qwerty123'].some(weak => val.toLowerCase().includes(weak)), 'A palavra-passe é muito fraca. Evite sequências comuns')
});


type loginData = z.infer<typeof loginShema>;


export default function Login(){

    const {register, handleSubmit, formState: {errors, isSubmitting}} = useForm<loginData>({
        resolver:zodResolver(loginShema)
    });

    const router = useRouter();
    async function handleLogin(data:loginData){
        try{
            console.log(data);

            // Simula envio para API
             await new Promise((res) => setTimeout(res, 1000)); 
             toast.success("Conta criada com sucesso!",{
                duration: 3000
             })
             setTimeout(()=>{
                router.push("/dashboard");
             }, 1000);
        }catch(error){
            toast.error("Erro ao criar conta.Tenta novamente",{
                duration:3000
            })
        }
    }


    return(
        <div className=" flex h-screen gap-4">

            <section className="hidden md:flex relative w-1/2 h-full overflow-hidden bg-[#F8960D] p-2 text-white">
                <div className="flex flex-col absolute p-4 justify-around h-full ml-4">
                    <div className="flex gap-2 items-center">
                        <div className="flex p-2 rounded-2xl bg-white/20 text-white  items-center">
                            <BsBroadcast size={30} />
                        </div>
                        <h2 className="font-bold text-2xl ">AnunciosLoc</h2>
                    </div>

                    <div className="flex flex-col space-y-4  ">
                        <h1 className="font-bold text-5xl">Painel do <br />
                            Gestor de Rede
                        </h1>
                        <p className="">Gestão de infraestruturas, utilizadores, anúncios e bónus do <br /> sistema de publicidade baseado em localização.</p>
                    </div>

                    <p className="">© 2026 AnunciosLoc · Versão 1.0</p>
                </div>
            </section>

            <section className="flex flex-col md:w-1/2 w-full h-full items-center justify-center ">
                 <div className="flex flex-col max-w-md w-full mx-auto gap-4 p-4 ">  
                    <div className="flex flex-col space-y-2 items-center ">
                        <div className="flex p-2 bg-[#F8960D]/20 rounded-2xl items-center text-[#F8960D]"> <BsBroadcast size={30} /></div>
                        <h2 className="text-2xl text-black font-semibold">Iniciar sessão</h2>
                        <p className="text-gray-600 ">Acesso restrito ao Gestor de Rede.</p>
                    </div>

                    <form onSubmit={handleSubmit(handleLogin)} className='space-y-2'>
                        <div className="flex flex-col space-y-1">
                            <label htmlFor="" className='text-black font-semibold'>E-mail</label>
                            <div className="relative">
                                <LuMail className='absolute left-3 top-1/2 -translate-y-1/2 text-gray-400' size={14}/>
                                <input {...register("email")} type="e-mail" disabled={isSubmitting} placeholder='admin@email.com' className="w-full h-10 pl-8 text-gray-900 rounded-lg shadow border border-gray-200 outline-amber-600" />
                            </div>
                            {errors.email &&  <p className="text-xs text-red-500">{errors.email.message}</p>}
                        </div>

                        <div className="flex flex-col space-y-1">
                            <label htmlFor="" className='text-black font-semibold'>Palavra-passe</label>
                            <div className="relative">
                              <LuLock className='absolute top-1/2 left-3 text-gray-400 -translate-y-1/2' size={14}/>
                              <input {...register("password")} disabled={isSubmitting} type="password" placeholder='••••••••' className="w-full h-10 pl-8 text-gray-900 rounded-lg shadow border border-gray-200 outline-amber-600"  />
                            </div>
                        </div>
                        <Link href={'/recuperar_senha'} className='flex justify-end text-sm text-amber-500 hover:underline transition text-right'>Esqueceu a senha?</Link>
                        <button type='submit' disabled={isSubmitting} className="h-10 p-2 w-full text-center mt-2 text-white font-semibold bg-[#F8960D]  rounded-lg shadow-lg cursor-pointer disabled:opacity-80 disabled:cursor-not-allowed">{isSubmitting ? "Entrar...":"Entrar"}</button>
                    </form>

                </div> 
            </section>


        </div>
    )
}