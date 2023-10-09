import { writable } from 'svelte/store';

// Create a writable store for the auth string
export const authStore = writable('');

// Function to update the auth string in the store
export function updateAuthString(authString) {
  authStore.set(authString);
}

// Function to get the current auth string value from the store
export function getAuthString() {
    let authString;
    authStore.subscribe(value => {
      authString = value;
    });
    return authString;
  }