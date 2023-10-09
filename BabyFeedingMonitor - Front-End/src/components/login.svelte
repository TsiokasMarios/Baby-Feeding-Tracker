<script>
    import { updateAuthString } from '../store';

    export let onLogin;
    
    
    let encodedAuthString;

    let baseURI = "http://localhost:8081/BabyFeedingTracker/feeding/";

    const login = async (event) => {
        let username = event.target.querySelector("#username").value
        let password = event.target.querySelector("#password").value
        let authString = `${username}:${password}`;
        encodedAuthString = btoa(authString);
        const response = await fetch(
            baseURI+"feeding"
            ,{
                headers:{
                    'Authorization': 'Basic ' + encodedAuthString,
                    'Content-Type': 'application/json'
                }
            }
        )
        if (response.status === 200) {
            updateAuthString(encodedAuthString); // Update the auth string in the store
            onLogin(true);
        }
    }
</script>

<div class="login">
    <form on:submit|preventDefault={login}>
        <h1>Login</h1>
        <input type="text" placeholder="Username" id="username" />
        <br />
        <input type="password" placeholder="Password" id="password"/>
        <br />
        <button class="login-button" >Login</button>
    </form>
</div>

<style>
    .login {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 90vh;
        text-align: center;
    }
    
    .login-button {
        margin-top: 10px; /* add some margin to the top of the button */
    }

</style>
