// Funciones para la ventana modal de ayuda

// Función para mostrar el modal de ayuda
function showHelpModal() {
    document.getElementById("helpModal").style.display = "block";
}

// Función para cerrar el modal de ayuda
function closeHelpModal() {
    document.getElementById("helpModal").style.display = "none";
}

// Inicializar el modal de ayuda cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    // Mostrar el modal de ayuda después de 1 segundo
    setTimeout(function() {
        showHelpModal();
        
        // Cerrar automáticamente después de 60 segundos
        setTimeout(function() {
            closeHelpModal();
        }, 60000);
    }, 1000);
    
    // Cerrar modal al hacer clic fuera de él
    window.addEventListener('click', function(event) {
        var helpModal = document.getElementById("helpModal");
        if (event.target == helpModal) {
            closeHelpModal();
        }
    });
    
    // Asignar evento al botón de cerrar
    var closeButton = document.querySelector('.help-modal-close');
    if (closeButton) {
        closeButton.addEventListener('click', closeHelpModal);
    }
    
    // Asignar evento al botón de entendido
    var entendidoButton = document.querySelector('.help-modal-footer button');
    if (entendidoButton) {
        entendidoButton.addEventListener('click', closeHelpModal);
    }
}); 