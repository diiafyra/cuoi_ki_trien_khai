import { initializeApp } from "https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js";
import { getAuth, GoogleAuthProvider, signInWithPopup } from "https://www.gstatic.com/firebasejs/9.6.1/firebase-auth.js";

// Firebase config
const firebaseConfig = {
  apiKey: "AIzaSyA22EmgTu3L4pbs1s34l5OjhD-qUxTbtuk",
  authDomain: "careful-century-453110-s8.firebaseapp.com",
  projectId: "careful-century-453110-s8",
  appId: "1:50093176860:web:3212750e1fa661080687d9"
};


// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const provider = new GoogleAuthProvider();

document.getElementById("googleSignInBtn").addEventListener("click", async () => {
  try {
    const result = await signInWithPopup(auth, provider);
    const user = result.user;
    const idToken = await user.getIdToken();

    // 🔁 Gửi idToken về backend
    const res = await fetch("http://localhost:8080/api/auth/google-login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ idToken })
    });

    const data = await res.json();
    console.log("Đăng nhập thành công:", data);
      localStorage.setItem('token', user.accessToken);
      localStorage.setItem('userId', user.id);
      window.location.href = 'connect-spotify.html';
  } catch (error) {
    console.error("Đăng nhập Google thất bại:", error);
  }
});
