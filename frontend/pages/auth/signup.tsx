import { useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { API_ENDPOINTS, API_BASE_URL } from '../../config/api';
import { FaEye, FaEyeSlash } from 'react-icons/fa';

const signupSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
});

type SignupForm = z.infer<typeof signupSchema>;

export default function Signup() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  
  const { register, handleSubmit, formState: { errors } } = useForm<SignupForm>({
    resolver: zodResolver(signupSchema)
  });

  const onSubmit = async (data: SignupForm) => {
    setIsLoading(true);
    setError('');
    
    try {
      const fullUrl = API_BASE_URL + API_ENDPOINTS.AUTH.SIGNUP;
      console.log("Invoking signup endpoint at:", fullUrl);
      const response = await fetch(fullUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        router.push('/auth/login?message=Registration successful. Please log in.');
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Failed to create account. Please try again.');
      }
    } catch (error) {
      console.error("Signup error:", error);
      setError('An unexpected error occurred. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: '#f9fafb',
      padding: '20px'
    }}>
      <div style={{
        maxWidth: '400px',
        width: '100%',
        backgroundColor: 'white',
        padding: '32px',
        borderRadius: '12px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
      }}>
        <h1 style={{
          textAlign: 'center',
          fontSize: '24px',
          fontWeight: 'bold',
          color: '#111827',
          marginBottom: '8px'
        }}>Create your account</h1>
        <p style={{
          textAlign: 'center',
          color: '#6b7280',
          fontSize: '14px',
          marginBottom: '24px'
        }}>Join us to start monitoring your heart rate</p>

        {error && (
          <div style={{
            padding: '12px',
            marginBottom: '16px',
            borderRadius: '8px',
            backgroundColor: '#fee2e2',
            color: '#dc2626',
            fontSize: '14px'
          }}>{error}</div>
        )}
        
        <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'flex', gap: '16px', width: '100%' }}>
            <input
              {...register('firstName')}
              type="text"
              placeholder="First name"
              style={{
                flex: 1,
                padding: '12px',
                borderRadius: '8px',
                border: '1px solid #e5e7eb',
                fontSize: '14px',
                outline: 'none',
                transition: 'border-color 0.2s, box-shadow 0.2s',
                boxSizing: 'border-box',
                margin: 0
              }}
              onFocus={(e) => {
                e.target.style.borderColor = '#3b82f6';
                e.target.style.boxShadow = '0 0 0 2px rgba(59, 130, 246, 0.1)';
              }}
              onBlur={(e) => {
                e.target.style.borderColor = '#e5e7eb';
                e.target.style.boxShadow = 'none';
              }}
            />
            <input
              {...register('lastName')}
              type="text"
              placeholder="Last name"
              style={{
                flex: 1,
                padding: '12px',
                borderRadius: '8px',
                border: '1px solid #e5e7eb',
                fontSize: '14px',
                outline: 'none',
                transition: 'border-color 0.2s, box-shadow 0.2s',
                boxSizing: 'border-box',
                margin: 0
              }}
              onFocus={(e) => {
                e.target.style.borderColor = '#3b82f6';
                e.target.style.boxShadow = '0 0 0 2px rgba(59, 130, 246, 0.1)';
              }}
              onBlur={(e) => {
                e.target.style.borderColor = '#e5e7eb';
                e.target.style.boxShadow = 'none';
              }}
            />
          </div>
          {errors.firstName && (
            <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '-8px', marginBottom: '0' }}>{errors.firstName.message}</p>
          )}
          {errors.lastName && (
            <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '-8px', marginBottom: '0' }}>{errors.lastName.message}</p>
          )}
          <input
            {...register('email')}
            type="email"
            placeholder="Email address"
            style={{
              width: '100%',
              padding: '12px',
              borderRadius: '8px',
              border: '1px solid #e5e7eb',
              fontSize: '14px',
              outline: 'none',
              transition: 'border-color 0.2s, box-shadow 0.2s',
              boxSizing: 'border-box',
              margin: 0
            }}
            onFocus={(e) => {
              e.target.style.borderColor = '#3b82f6';
              e.target.style.boxShadow = '0 0 0 2px rgba(59, 130, 246, 0.1)';
            }}
            onBlur={(e) => {
              e.target.style.borderColor = '#e5e7eb';
              e.target.style.boxShadow = 'none';
            }}
          />
          {errors.email && (
            <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '-8px', marginBottom: '0' }}>{errors.email.message}</p>
          )}
          <div style={{ position: 'relative', width: '100%' }}>
            <input
              {...register('password')}
              type={showPassword ? 'text' : 'password'}
              placeholder="Password"
              style={{
                width: '100%',
                boxSizing: 'border-box',
                padding: '12px',
                borderRadius: '8px',
                border: '1px solid #e5e7eb',
                fontSize: '14px',
                outline: 'none',
                transition: 'border-color 0.2s, box-shadow 0.2s',
                margin: 0
              }}
              onFocus={(e) => {
                e.target.style.borderColor = '#3b82f6';
                e.target.style.boxShadow = '0 0 0 2px rgba(59, 130, 246, 0.1)';
              }}
              onBlur={(e) => {
                e.target.style.borderColor = '#e5e7eb';
                e.target.style.boxShadow = 'none';
              }}
            />
            <span
              onClick={() => setShowPassword((prev) => !prev)}
              style={{
                position: 'absolute',
                right: '16px',
                top: '50%',
                transform: 'translateY(-50%)',
                cursor: 'pointer',
                color: '#6b7280',
                fontSize: '18px',
                background: 'transparent',
                padding: 0,
                lineHeight: 1
              }}
              aria-label={showPassword ? 'Hide password' : 'Show password'}
              tabIndex={0}
              onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') setShowPassword((prev) => !prev); }}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
          {errors.password && (
            <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '-8px', marginBottom: '0' }}>{errors.password.message}</p>
          )}
          <button
            type="submit"
            disabled={isLoading}
            style={{
              width: '100%',
              padding: '12px',
              backgroundColor: '#3b82f6',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontSize: '14px',
              fontWeight: '500',
              cursor: 'pointer',
              transition: 'background-color 0.2s, transform 0.1s',
              transform: 'translateY(0)',
              margin: 0
            }}
            onMouseOver={(e) => {
              if (!isLoading) {
                e.currentTarget.style.backgroundColor = '#2563eb';
                e.currentTarget.style.transform = 'translateY(-1px)';
              }
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.backgroundColor = '#3b82f6';
              e.currentTarget.style.transform = 'translateY(0)';
            }}
            onMouseDown={(e) => {
              if (!isLoading) {
                e.currentTarget.style.transform = 'translateY(0)';
              }
            }}
          >
            {isLoading ? 'Creating account...' : 'Create account'}
          </button>

          <div style={{ textAlign: 'center', marginTop: '8px' }}>
            <p style={{ color: '#6b7280', fontSize: '14px' }}>
              Already have an account?{' '}
              <Link 
                href="/auth/login"
                style={{
                  color: '#3b82f6',
                  textDecoration: 'none',
                  fontWeight: '500',
                  transition: 'color 0.2s'
                }}
                onMouseOver={(e) => e.currentTarget.style.color = '#2563eb'}
                onMouseOut={(e) => e.currentTarget.style.color = '#3b82f6'}
              >
                Sign in
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
} 