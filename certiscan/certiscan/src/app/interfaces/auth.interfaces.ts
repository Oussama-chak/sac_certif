export interface SignInRequest {
    username: string;
    password: string;
  }
  
  export interface AuthResponse {
    token: string;
    userId: string;
    role: string;
  }
  
  export interface UserData {
    userId: string;
    role: string;
  }