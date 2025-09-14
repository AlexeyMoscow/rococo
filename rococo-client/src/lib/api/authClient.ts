const BASE_URL = `${import.meta.env.VITE_AUTH_URL}`;

export const authClient = {
    getToken: async(url: string, data: URLSearchParams) => {
        const response = await fetch(`${BASE_URL}/${url}`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-type": "application/x-www-form-urlencoded",
            },
            body: data.toString()
        });
        if (!response.ok) {
            throw new Error("Failed loading data");
        }
        
        const token = await response.json();
        try {
            if (token && typeof token === 'object') {
                if (token.id_token) localStorage.setItem('id_token', token.id_token);
                if (token.access_token) localStorage.setItem('access_token', token.access_token);
            }
        } catch (_) {}
        return token;
        
    },
    logout: async () => {
        const response = await fetch(`${BASE_URL}/logout`, { method: "GET", credentials: "include" });
        if (!response.ok) { throw new Error("Failed loading data"); }
        try { localStorage.removeItem("id_token"); localStorage.removeItem("access_token"); } catch (_) {}
    }
}
