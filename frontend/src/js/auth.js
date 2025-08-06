const loginForm = document.getElementById('loginForm');
if (loginForm) {
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    const email = form.email.value;
    const password = form.password.value;

    const result = await loginUser(email, password);
    if (result.success) {
      const user = result.data;
      localStorage.setItem('token', user.accessToken);
      localStorage.setItem('userId', user.id);
      window.location.href = 'connect-spotify.html';
    } else {
      alert(result.message || 'Login failed');
    }
  });
}

const registerForm = document.getElementById('registerForm');
if (registerForm) {
  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    const name = form.name.value;
    const email = form.email.value;
    const password = form.password.value;

    const result = await registerUser(name, email, password);
    if (result.success) {
      const user = result.data;
      localStorage.setItem('token', user.accessToken);
      localStorage.setItem('userId', user.id);
      window.location.href = 'connect-spotify.html';
    } else {
      alert(result.message || 'Registration failed');
    }
  });
}
