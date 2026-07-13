// Modal de Redimensionar
export function ModalRedimensionar({
    isOpen,
    onClose,
    onSubmit,
    register,
    errors,
    isSubmitting,
    infra
}: any) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold">Redimensionar</h2>
                    <button onClick={onClose} className="p-1 hover:bg-gray-100 rounded transition">
                        
                    </button>
                </div>

                {infra && (
                    <p className="text-sm text-gray-500 mb-4">
                        Redimensionando: <span className="font-semibold text-gray-700">{infra.infraestrutura}</span>
                    </p>
                )}

                <form onSubmit={onSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Capacidade</label>
                        <input
                            {...register('capacidade')}
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="100"
                        />
                        {errors.capacidade && <p className="text-xs text-red-500 mt-1">{errors.capacidade.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Bônus Entrega</label>
                        <input
                            {...register('bonus_entrega')}
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="10"
                        />
                        {errors.bonus_entrega && <p className="text-xs text-red-500 mt-1">{errors.bonus_entrega.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Custo por Post</label>
                        <input
                            {...register('custo_post')}
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="5"
                        />
                        {errors.custo_post && <p className="text-xs text-red-500 mt-1">{errors.custo_post.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Raio</label>
                        <input
                            {...register('raio')}
                            type="number"
                            step="0.01"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="20.5"
                        />
                        {errors.raio && <p className="text-xs text-red-500 mt-1">{errors.raio.message}</p>}
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
                            className="flex-1 px-4 py-2 rounded-lg bg-blue-500 text-white font-semibold hover:bg-blue-600 transition disabled:opacity-50"
                        >
                            {isSubmitting ? 'Redimensionando...' : 'Redimensionar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}