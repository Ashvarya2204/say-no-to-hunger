document.addEventListener("DOMContentLoaded", function () {

    const success = document.body.getAttribute("data-success");
    const error = document.body.getAttribute("data-error");

    if (success) {
        Swal.fire({
            icon: "success",
            title: "Success",
            text: success,
            confirmButtonText: "OK",
            confirmButtonColor: "#FF8C00",
            background: "#FFF8E1",
            color: "#333",
            iconColor: "#FFA500",
            customClass: {
                popup: "sn2h-popup"
            }
        });
    }

    if (error) {
        Swal.fire({
            icon: "error",
            title: "Oops!",
            text: error,
            confirmButtonText: "Try Again",
            confirmButtonColor: "#FF6F00",
            background: "#FFF3E0",
            iconColor: "#FF6F00"
        });
    }

});