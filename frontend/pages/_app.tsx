import '../styles/globals.css';
import type { AppProps } from 'next/app';
import TopMenuBar from '../components/TopMenuBar';
import { useEffect, useState } from 'react';
import { API_BASE_URL } from '../config/api';

export default function MyApp({ Component, pageProps }: AppProps) {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
    if (!token) {
      setUser(null);
      return;
    }
    const fetchUser = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/api/users/current-user`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
          const userData = await response.json();
          setUser(userData);
        } else {
          setUser(null);
        }
      } catch {
        setUser(null);
      }
    };
    fetchUser();
  }, []);

  return (
    <>
      <TopMenuBar user={user} />
      <div style={{ paddingTop: '56px' }}>
        <Component {...pageProps} />
      </div>
    </>
  );
} 