import { User } from './User';

export class UserService {
    apiUrl = process.env.JAVA_API_URL + '/api/users';

    async fetchUser(): Promise<User>{
        return async () => {
        try {
            const response = await fetch(this.apiUrl, {
                method: 'GET'
            });
            if (response.ok) {
                return response.body;
            } else {
                console.error(response.status + " " + response.body);
                return null;
            }
        } catch (e) {
            console.error(e);
            throw e;
        }
    };
    }
    
    
}