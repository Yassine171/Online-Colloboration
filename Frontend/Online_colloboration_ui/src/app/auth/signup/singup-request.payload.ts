export interface SignupRequestPayload {
    username: string;
    password: string;
    email: string;
    roles?: {
      id?: number;
      name: string;
      description?: string;
    }[];
}
