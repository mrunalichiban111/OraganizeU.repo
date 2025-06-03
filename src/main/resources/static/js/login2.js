
   function updateGreeting() {
       event.preventDefault();
       const usernameInput = document.getElementById('input1');
       const username = usernameInput.value;
       localStorage.setItem('greeting',username);
       window.location.href='index.html';

   }