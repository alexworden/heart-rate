import { useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import axios from 'axios';
import { API_BASE_URL } from '../config/api';

export default function AddItem() {
  const router = useRouter();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!name || !description) {
        setError('Please fill in Name and Description.');
        return;
    }

    if (!imageUrl) {
      setError('Please provide an image URL.');
      return;
    }

    setLoading(true);

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        router.replace('/auth/login');
        return;
      }

      const itemData = {
        name,
        description,
        imageUrl,
      };
      const response = await axios.post(`${API_BASE_URL}/api/items/from-url`, itemData, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.status === 201) {
        setSuccess('Item added successfully!');
        setName('');
        setDescription('');
        setImageUrl('');
        setTimeout(() => {
          router.push('/');
        }, 2000);
      } else {
        setError('Failed to add item. Please try again.');
      }
    } catch (err) {
      console.error('Error adding item:', err);
      if (axios.isAxiosError(err) && err.response) {
        if (err.response.status === 400) {
             setError('Invalid input. Please check the form data.');
        } else if (err.response.status === 401) {
            setError('Unauthorized. Please log in.');
            router.replace('/auth/login');
        } else {
             setError(`An error occurred: ${err.response.status} ${err.response.statusText}`);
        }
      } else {
          setError('An unexpected error occurred while adding the item.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#111827', // Dark background
      color: '#f3f4f6', // Light text
      paddingTop: '5rem', // Account for fixed navbar
      padding: '2rem'
    }}>
      {/* Navigation Bar Placeholder (can be replaced with a shared component later) */}
      <nav style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '1rem 2rem',
        backgroundColor: '#1f2937', // Slightly lighter dark
        borderBottom: '1px solid #374151',
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        zIndex: 50
      }}>
        {/* Logo/Title */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem'
        }}>
          <Link href="/" style={{
            fontSize: '1.5rem',
            fontWeight: 'bold',
            background: 'linear-gradient(90deg, #3b82f6, #60a5fa)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            textDecoration: 'none'
          }}>
            HeartRate
          </Link>
        </div>
         {/* Add Item Link - Placeholder for now */}
        <div>
          <Link href="/" style={{
            color: '#9ca3af',
            textDecoration: 'none',
            marginLeft: '1rem'
          }}>
            Home
          </Link>
        </div>
      </nav>

      {/* Main Content */}
      <main style={{
        maxWidth: '32rem',
        margin: '0 auto',
        backgroundColor: '#1f2937',
        padding: '2rem',
        borderRadius: '0.5rem',
        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
      }}>
        <h1 style={{
          fontSize: '2rem',
          fontWeight: 'bold',
          marginBottom: '1.5rem',
          textAlign: 'center',
          background: 'linear-gradient(90deg, #3b82f6, #60a5fa)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent'
        }}>
          Add New Item
        </h1>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1rem' }}>
            <label htmlFor="name" style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: '#d1d5db' }}>
              Name
            </label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              style={{
                width: '100%',
                padding: '0.5rem',
                borderRadius: '0.25rem',
                border: '1px solid #4b5563',
                backgroundColor: '#374151',
                color: '#f3f4f6',
                fontSize: '1rem'
              }}
              required
            />
          </div>

          <div style={{ marginBottom: '1rem' }}>
            <label htmlFor="description" style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: '#d1d5db' }}>
              Description
            </label>
            <textarea
              id="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={4}
              style={{
                width: '100%',
                padding: '0.5rem',
                borderRadius: '0.25rem',
                border: '1px solid #4b5563',
                backgroundColor: '#374151',
                color: '#f3f4f6',
                fontSize: '1rem'
              }}
              required
            ></textarea>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <label htmlFor="imageUrl" style={{ display: 'block', fontSize: '0.875rem', color: '#d1d5db' }}>
                Image URL
              </label>
            </div>
            <input
              type="text"
              id="imageUrl"
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
              style={{
                width: '100%',
                padding: '0.5rem',
                borderRadius: '0.25rem',
                border: '1px solid #4b5563',
                backgroundColor: '#374151',
                color: '#f3f4f6',
                fontSize: '1rem'
              }}
              required
            />
          </div>

          {error && (
            <div style={{ color: '#f87171', marginBottom: '1rem', textAlign: 'center' }}>
              {error}
            </div>
          )}

          {success && (
            <div style={{ color: '#4ade80', marginBottom: '1rem', textAlign: 'center' }}>
              {success}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            style={{
              width: '100%',
              padding: '0.75rem',
              backgroundColor: loading ? '#6b7280' : '#3b82f6',
              color: 'white',
              fontWeight: 'bold',
              borderRadius: '0.375rem',
              border: 'none',
              cursor: loading ? 'not-allowed' : 'pointer',
              transition: 'background-color 0.2s'
            }}
            onMouseOver={(e) => !loading && (e.currentTarget.style.backgroundColor = '#60a5fa')}
            onMouseOut={(e) => !loading && (e.currentTarget.style.backgroundColor = '#3b82f6')}
          >
            {loading ? 'Adding Item...' : 'Add Item'}
          </button>
        </form>
      </main>
    </div>
  );
} 