// app/components/infraestruturas/ModalInfraestrutura.tsx
'use client'

import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { LuX } from "react-icons/lu";
import toast from "react-hot-toast";

const sanitizeText = (value: string) => {
    return value
        .trim()
        .replace(/\s+/g, " ")
        .replace(/[<>'"]/g, "")
        .slice(0, 100);
};

// 🔥 Schema com tipos explícitos
const schema = z.object({
    nome: z.string()
        .transform(sanitizeText)
        .pipe(
            z.string()
                .min(2, "Nome obrigatório")
                .max(80, "Máximo 80 caracteres")
        ),

    gps: z.boolean().default(false),

    wifi: z.boolean().default(false),

    latitude: z.string().optional().default(""),

    longitude: z.string().optional().default(""),

    raio: z.string().optional().default(""),

    ssids: z.string().optional().default(""),

    capacidade: z.number().min(1, "Capacidade inválida").default(1), // ← SEM coerce

    premio: z.number().min(0, "Prémio inválido").default(0), // ← SEM coerce

    tipoRestricao: z.enum(["POST", "ENTREGA"]).optional().default("POST"),

    regras: z.string().optional().default("")

}).refine(
    data => data.gps || data.wifi,
    {
        path: ["gps"],
        message: "Selecione GPS ou WiFi"
    }
);

// 🔥 Tipo explícito para o formulário
type FormData = z.infer<typeof schema>;

export interface Infraestrutura {
    id: number;
    nome: string;
    gps: boolean;
    wifi: boolean;
    latitude?: string;
    longitude?: string;
    raio?: string;
    ssids?: string;
    capacidade: number;
    premio: number;
    tipoRestricao?: "POST" | "ENTREGA";
    regras?: string;
}

interface Props {
    open: boolean;
    onClose: () => void;
    onSave: (data: Infraestrutura) => void;
    infraestrutura?: Infraestrutura | null;
}

export default function ModalInfraestrutura({
    open,
    onClose,
    onSave,
    infraestrutura
}: Props) {

    const {
        register,
        watch,
        handleSubmit,
        formState: { errors, isSubmitting }
    } = useForm<FormData>({
        resolver: zodResolver(schema),
        defaultValues: {
            nome: infraestrutura?.nome ?? "",
            gps: infraestrutura?.gps ?? false,
            wifi: infraestrutura?.wifi ?? false,
            latitude: infraestrutura?.latitude ?? "",
            longitude: infraestrutura?.longitude ?? "",
            raio: infraestrutura?.raio ?? "",
            ssids: infraestrutura?.ssids ?? "",
            capacidade: infraestrutura?.capacidade ?? 1,
            premio: infraestrutura?.premio ?? 0,
            tipoRestricao: infraestrutura?.tipoRestricao ?? "POST",
            regras: infraestrutura?.regras ?? ""
        }
    });

    const gps = watch("gps");
    const wifi = watch("wifi");

    async function submit(data: FormData) {
        await new Promise(resolve => setTimeout(resolve, 1000));

        onSave({
            id: infraestrutura?.id ?? Date.now(),
            ...data,
            tipoRestricao: data.tipoRestricao || "POST",
        });

        toast.success(
            infraestrutura
                ? "Infraestrutura atualizada"
                : "Infraestrutura criada"
        );

        onClose();
    }

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">

            <div className="bg-white w-full max-w-2xl rounded-2xl shadow-xl p-6">

                <div className="flex justify-between items-center mb-6">

                    <h2 className="text-2xl font-semibold">
                        {infraestrutura
                            ? "Editar Infraestrutura"
                            : "Nova Infraestrutura"}
                    </h2>

                    <button onClick={onClose}>
                        <LuX size={22} />
                    </button>

                </div>

                <form
                    onSubmit={handleSubmit(submit)}
                    className="space-y-4"
                >

                    <div>
                        <label className="font-medium">Nome</label>

                        <input
                            {...register("nome")}
                            className="w-full rounded-lg p-2 shadow border border-gray-200 outline-amber-600 mt-1"
                        />

                        {errors.nome && (
                            <p className="text-red-500 text-sm">
                                {errors.nome.message}
                            </p>
                        )}
                    </div>

                    <div className="flex gap-6">

                        <label className="flex gap-2">
                            <input
                                type="checkbox"
                                {...register("gps")}
                            />
                            GPS
                        </label>

                        <label className="flex gap-2">
                            <input
                                type="checkbox"
                                {...register("wifi")}
                            />
                            WiFi
                        </label>

                    </div>

                    {errors.gps && (
                        <p className="text-red-500 text-sm">
                            {errors.gps.message}
                        </p>
                    )}

                    {gps && (

                        <div className="grid md:grid-cols-3 gap-4">

                            <input
                                {...register("latitude")}
                                placeholder="Latitude"
                                className="rounded-lg p-2 shadow border border-gray-200 outline-amber-600"
                            />

                            <input
                                {...register("longitude")}
                                placeholder="Longitude"
                                className="rounded-lg p-2 shadow border border-gray-200 outline-amber-600"
                            />

                            <input
                                {...register("raio")}
                                placeholder="Raio (metros)"
                                className="rounded-lg p-2 shadow border border-gray-200 outline-amber-600"
                            />

                        </div>

                    )}

                    {wifi && (

                        <textarea
                            {...register("ssids")}
                            placeholder="SSID1, SSID2, SSID3"
                            className="w-full rounded-lg p-2 shadow border border-gray-200 outline-amber-600"
                        />

                    )}

                    <div className="grid md:grid-cols-2 gap-4">

                        <div>
                            <input
                                type="number"
                                {...register("capacidade", { valueAsNumber: true })}
                                placeholder="Capacidade"
                                className="w-full rounded-lg p-2 shadow border border-gray-200 outline-amber-600"
                            />
                            {errors.capacidade && (
                                <p className="text-red-500 text-sm">
                                    {errors.capacidade.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <input
                                type="number"
                                {...register("premio", { valueAsNumber: true })}
                                placeholder="Prémio"
                                className="w-full rounded-lg p-2 shadow border border-gray-200 outline-amber-600"
                            />
                            {errors.premio && (
                                <p className="text-red-500 text-sm">
                                    {errors.premio.message}
                                </p>
                            )}
                        </div>

                    </div>

                    <div>
                        <label className="font-medium">
                            Tipo de Restrição
                        </label>

                        <select
                            {...register("tipoRestricao")}
                            className="w-full rounded-lg p-2 shadow border border-gray-200 outline-amber-600 mt-1"
                        >
                            <option value="POST">
                                Restrição de Post
                            </option>

                            <option value="ENTREGA">
                                Restrição de Entrega
                            </option>
                        </select>
                    </div>

                    <div>
                        <label className="font-medium">
                            Regras
                        </label>

                        <textarea
                            rows={4}
                            {...register("regras")}
                            placeholder="Ex.: Excluir anúncios de redes televisivas"
                            className="w-full rounded-lg p-2 shadow border border-gray-200 outline-amber-600 mt-1"
                        />
                    </div>

                    <div className="flex justify-end gap-3">

                        <button
                            type="button"
                            onClick={onClose}
                            className="px-5 py-2 border rounded-lg"
                        >
                            Cancelar
                        </button>

                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="px-5 py-2 bg-amber-500 text-white rounded-lg"
                        >
                            {isSubmitting
                                ? "Guardando..."
                                : "Guardar"}
                        </button>

                    </div>

                </form>

            </div>

        </div>
    )
}