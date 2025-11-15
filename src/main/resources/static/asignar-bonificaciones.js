// JS para Asignar Bonificaciones

document.addEventListener('DOMContentLoaded', () => {
    cargarBonificaciones();
    conectarSSE();
});

function cargarBonificaciones() {
    fetch('/asignarBonificaciones')
        .then(res => res.json())
        .then(bonificaciones => {
            const select = document.getElementById('bonificacionSelect');
            select.innerHTML = '';
            if (bonificaciones.length === 0) {
                select.innerHTML = '<option value="">No hay bonificaciones</option>';
            } else {
                bonificaciones.forEach(b => {
                    const opt = document.createElement('option');
                    opt.value = b.nombre;
                    opt.textContent = b.nombre;
                    select.appendChild(opt);
                });
            }
        })
        .catch(() => {
            document.getElementById('bonificacionSelect').innerHTML = '<option value="">Error al cargar</option>';
        });
}

function conectarSSE() {
    // Requiere sesión admin activa
    const sseStatus = document.getElementById('sseStatus');
    const eventSource = new EventSource('/asignarBonificaciones/registrarSSE');
    sseStatus.textContent = 'Conectado a SSE para bonificaciones';
    eventSource.onmessage = function(event) {
        try {
            const bonificaciones = JSON.parse(event.data);
            actualizarBonificaciones(bonificaciones);
            sseStatus.textContent = 'Actualización recibida vía SSE';
        } catch (e) {
            sseStatus.textContent = 'Error en datos SSE';
        }
    };
    eventSource.onerror = function() {
        sseStatus.textContent = 'Desconectado de SSE';
    };
}

function actualizarBonificaciones(bonificaciones) {
    const select = document.getElementById('bonificacionSelect');
    select.innerHTML = '';
    if (bonificaciones.length === 0) {
        select.innerHTML = '<option value="">No hay bonificaciones</option>';
    } else {
        bonificaciones.forEach(b => {
            const opt = document.createElement('option');
            opt.value = b.nombre;
            opt.textContent = b.nombre;
            select.appendChild(opt);
        });
    }
}
