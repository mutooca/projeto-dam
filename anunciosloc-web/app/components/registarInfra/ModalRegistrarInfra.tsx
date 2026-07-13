// Modal de Registro de Infraestrutura
export function ModalRegistrarInfra({
    isOpen,
    onClose,
    onSubmit,
    register,
    errors,
    isSubmitting,
    watch,
    control,
    fields,
    append,
    remove,
    isGPS,
    isWiFi
}: any) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold">Registar Infraestrutura</h2>
                    <button onClick={onClose} className="p-1 hover:bg-gray-100 rounded transition">
                        
                    </button>
                </div>

                <form onSubmit={onSubmit} className="space-y-4">
                    {/* Nome */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Nome</label>
                        <input
                            {...register('nome')}
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500 focus:border-amber-500"
                            placeholder="Digite o nome da infraestrutura"
                        />
                        {errors.nome && <p className="text-xs text-red-500 mt-1">{errors.nome.message}</p>}
                    </div>

                    {/* Tipo de Coordenadas - Checkboxes */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">Tipo de coordenadas</label>
                        <div className="flex gap-6">
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input
                                    type="checkbox"
                                    value="GPS"
                                    {...register('tipo_coordenadas')}
                                    className="w-4 h-4 accent-amber-500"
                                />
                                <span className="text-sm font-medium">GPS</span>
                            </label>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input
                                    type="checkbox"
                                    value="WiFi"
                                    {...register('tipo_coordenadas')}
                                    className="w-4 h-4 accent-amber-500"
                                />
                                <span className="text-sm font-medium">WiFi</span>
                            </label>
                        </div>
                        {errors.tipo_coordenadas && <p className="text-xs text-red-500 mt-1">{errors.tipo_coordenadas.message}</p>}
                    </div>

                    {/* Campos GPS */}
                    {isGPS && (
                        <div className="bg-gray-50 p-4 rounded-lg space-y-3 border border-gray-200">
                            <h3 className="font-semibold text-sm text-gray-700">Coordenadas GPS</h3>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Latitude</label>
                                    <input
                                        {...register('latitude')}
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="-8.8147"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Longitude</label>
                                    <input
                                        {...register('longitude')}
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="13.2302"
                                    />
                                </div>
                                <div className="col-span-2">
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Raio</label>
                                    <input
                                        {...register('raio')}
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="20"
                                    />
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Campos WiFi */}
                    {isWiFi && (
                        <div className="bg-gray-50 p-4 rounded-lg space-y-3 border border-gray-200">
                            <h3 className="font-semibold text-sm text-gray-700">Dados WiFi</h3>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">SSID</label>
                                    <input
                                        {...register('ssid')}
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="SSID da rede"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Nome</label>
                                    <input
                                        {...register('nome_wifi')}
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="Nome da rede"
                                    />
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Prêmio de entrega */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Prémio de entrega (pts)</label>
                        <input
                            {...register('premio_entrega')}
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="5"
                        />
                        {errors.premio_entrega && <p className="text-xs text-red-500 mt-1">{errors.premio_entrega.message}</p>}
                    </div>

                    {/* Restrições */}
                    <div>
                        <div className="flex justify-between items-center mb-2">
                            <label className="block text-sm font-semibold text-gray-700">Restrições</label>
                            <button
                                type="button"
                                onClick={() => append({ tipo_restricao: '', valor: '', descricao: '' })}
                                className="text-amber-500 text-sm font-semibold hover:text-amber-600 flex items-center gap-1"
                            >
                                 Adicionar
                            </button>
                        </div>
                        {fields.map((field: any, index: number) => (
                            <div key={field.id} className="bg-gray-50 p-3 rounded-lg mb-2 border border-gray-200">
                                <div className="flex justify-between items-start">
                                    <h4 className="text-xs font-medium text-gray-500">Restrição #{index + 1}</h4>
                                    {index > 0 && (
                                        <button
                                            type="button"
                                            onClick={() => remove(index)}
                                            className="text-red-500 hover:text-red-600"
                                        >
                                            
                                        </button>
                                    )}
                                </div>
                                <div className="grid grid-cols-3 gap-2 mt-1">
                                    <div>
                                        <label className="block text-xs text-gray-600">Tipo</label>
                                        <input
                                            {...register(`restricoes.${index}.tipo_restricao`)}
                                            className="w-full px-2 py-1 text-sm rounded border border-gray-300 focus:outline-amber-500"
                                            placeholder="post/entrega"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs text-gray-600">Valor</label>
                                        <input
                                            {...register(`restricoes.${index}.valor`)}
                                            className="w-full px-2 py-1 text-sm rounded border border-gray-300 focus:outline-amber-500"
                                            placeholder="Excluir redes..."
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs text-gray-600">Descrição</label>
                                        <input
                                            {...register(`restricoes.${index}.descricao`)}
                                            className="w-full px-2 py-1 text-sm rounded border border-gray-300 focus:outline-amber-500"
                                            placeholder="Descrição opcional"
                                        />
                                    </div>
                                </div>
                            </div>
                        ))}
                        {errors.restricoes && <p className="text-xs text-red-500 mt-1">{errors.restricoes.message}</p>}
                    </div>

                    <div className="flex gap-3 pt-4 border-t border-gray-200">
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 px-4 py-2 rounded-lg border border-gray-300 text-gray-700 font-semibold hover:bg-gray-50 transition"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-1 px-4 py-2 rounded-lg bg-amber-500 text-white font-semibold hover:bg-amber-600 transition disabled:opacity-50"
                        >
                            {isSubmitting ? 'Registrando...' : 'Registar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}