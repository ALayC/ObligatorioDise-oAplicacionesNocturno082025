/* 
 * sse.js - Librería para Server-Sent Events
 * Se asume que está incluida vistaWeb.js
 */

// Variable de configuración (se setea en cada página HTML)
var urlRegistroSSE = null;

// Esta función la llama vistaWeb.js automáticamente después del primer submit
function primerSubmitFinalizado(){
    registrarSSE();
}

function registrarSSE(){
    if (urlRegistroSSE === null) return;
    console.log("Registrando SSE en:", urlRegistroSSE);
    const eventSource = new EventSource(urlRegistroSSE, {withCredentials: true});
    eventSource.onmessage = function (event){
        try {
            console.log("📨 Mensaje SSE recibido:", event.data);
            const json = JSON.parse(event.data);
            procesarMensajeSSE(json);
        } catch(e){
            console.error("❌ Error parseando mensaje SSE:", e, event.data);
        }
    };
    eventSource.onerror = function (event){
        console.warn("⚠️ Conexión SSE cerrada o error:", event);
        eventSource.close();
        try {
            conexionSSECerrada(event);
        } catch (e) {
            console.error("Conexión SSE cerrada sin handler personalizado");
        }
    };
}

// Por defecto procesa mensajes SSE igual que respuestas de submit
function procesarMensajeSSE(mensaje){
    procesarResultadosSubmit(mensaje);
}
