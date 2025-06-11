// Import jQuery
const $ = require("jquery")

$(document).ready(() => {
  // Page entrance animation
  setTimeout(() => {
    $("#loginPage").addClass("active")
  }, 100)

  // Form submission
  $("#loginForm").on("submit", (e) => {
    e.preventDefault()

    const email = $("#email").val()
    const password = $("#password").val()
    const remember = $("#remember").is(":checked")

    // Show loading state
    showLoading()
    hideError()

    // Simulate API call
    $.ajax({
      url: "/api/login", // Replace with your actual API endpoint
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        email: email,
        password: password,
        remember: remember,
      }),
      success: (response) => {
        // Store auth token if provided
        if (response.token) {
          localStorage.setItem("authToken", response.token)
        }

        showSuccess()

        // Redirect to dashboard after a short delay
        setTimeout(() => {
          window.location.href = "dashboard.html"
        }, 1000)
      },
      error: (xhr) => {
        hideLoading()
        const errorMessage = xhr.responseJSON?.message || "Login failed. Please check your credentials."
        showError(errorMessage)
      },
    })
  })

  // Input animations
  $(".form-input").on("focus", function () {
    $(this).parent().addClass("focused")
  })

  $(".form-input").on("blur", function () {
    $(this).parent().removeClass("focused")
  })

  // Button hover effects
  $(".btn").on("mouseenter", function () {
    $(this).css("transform", "translateY(-2px) scale(1.02)")
  })

  $(".btn").on("mouseleave", function () {
    if (!$(this).is(":disabled")) {
      $(this).css("transform", "translateY(0) scale(1)")
    }
  })

  function showLoading() {
    $("#loginBtn").prop("disabled", true)
    $("#btnText").text("Signing in...")
    $("#btnSpinner").show()
  }

  function hideLoading() {
    $("#loginBtn").prop("disabled", false)
    $("#btnText").text("Sign in")
    $("#btnSpinner").hide()
  }

  function showSuccess() {
    $("#btnText").text("Success!")
    $("#btnSpinner").hide()
    $("#loginBtn").css("background", "linear-gradient(to right, #10b981, #059669)")
  }

  function showError(message) {
    $("#errorMessage").text(message).addClass("show")

    // Hide error after 5 seconds
    setTimeout(() => {
      hideError()
    }, 5000)
  }

  function hideError() {
    $("#errorMessage").removeClass("show")
  }

  // Add some interactive effects
  $(".card-content").on("mouseenter", () => {
    $(".card-glow").css("opacity", "1")
  })

  $(".card-content").on("mouseleave", () => {
    $(".card-glow").css("opacity", "0.75")
  })
})
