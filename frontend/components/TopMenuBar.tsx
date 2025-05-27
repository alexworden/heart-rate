import Link from 'next/link';
import { useRouter } from 'next/router';
import { FaHome, FaPlusCircle, FaStar, FaUserCircle } from 'react-icons/fa';
import { useState } from 'react';

interface User {
  firstName: string;
  lastName: string;
}

const menuItems = [
  { href: '/', icon: <FaHome />, label: 'Home' },
  { href: '/add-item', icon: <FaPlusCircle />, label: 'Add Item' },
  { href: '/recommendations', icon: <FaStar />, label: 'Recommendations' },
];

export default function TopMenuBar({ user }: { user?: User | null }) {
  const router = useRouter();
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const initials = user ? `${user.firstName[0] || ''}${user.lastName[0] || ''}` : '';

  return (
    <nav style={{
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '56px',
      background: '#1f2937',
      borderBottom: '1px solid #374151',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      zIndex: 1000,
      boxShadow: '0 2px 8px rgba(0,0,0,0.10)'
    }}>
      {/* Left: Logo/Title */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginLeft: '2rem' }}>
        <span style={{
          fontSize: '1.5rem',
          fontWeight: 'bold',
          background: 'linear-gradient(90deg, #60a5fa, #3b82f6)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          cursor: 'pointer',
          letterSpacing: '1px',
        }}
          onClick={() => router.push('/')}
        >
          HeartRate
        </span>
      </div>
      {/* Center: Menu Items */}
      <div style={{ display: 'flex', gap: '40px' }}>
        {menuItems.map(item => (
          <Link key={item.href} href={item.href} legacyBehavior>
            <a
              style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                color: router.pathname === item.href ? '#60a5fa' : '#cbd5e1',
                fontSize: '22px',
                textDecoration: 'none',
                fontWeight: router.pathname === item.href ? 600 : 400,
                transition: 'color 0.2s, background 0.2s',
                padding: '4px 12px',
                borderRadius: '8px',
                background: router.pathname === item.href ? 'rgba(59,130,246,0.10)' : 'transparent',
                cursor: 'pointer',
              }}
              aria-label={item.label}
              onMouseOver={e => e.currentTarget.style.background = 'rgba(59,130,246,0.15)'}
              onMouseOut={e => e.currentTarget.style.background = router.pathname === item.href ? 'rgba(59,130,246,0.10)' : 'transparent'}
            >
              {item.icon}
              <span style={{ fontSize: '12px', marginTop: '2px', color: '#cbd5e1' }}>{item.label}</span>
            </a>
          </Link>
        ))}
      </div>
      {/* Right: User Icon and Dropdown */}
      <div style={{ position: 'relative', marginRight: '2rem' }}>
        <button
          onClick={() => setIsProfileOpen((open) => !open)}
          style={{
            background: 'none',
            border: 'none',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            padding: '4px',
            borderRadius: '50%',
            transition: 'background 0.2s',
          }}
          aria-label="User menu"
        >
          <FaUserCircle size={32} color="#60a5fa" />
          {initials && <span style={{ fontWeight: 600, color: '#cbd5e1', fontSize: '1rem', letterSpacing: '1px' }}>{initials}</span>}
        </button>
        {isProfileOpen && (
          <div style={{
            position: 'absolute',
            right: 0,
            top: '110%',
            background: '#232946',
            border: '1px solid #374151',
            borderRadius: '8px',
            boxShadow: '0 4px 16px rgba(0,0,0,0.18)',
            minWidth: '160px',
            zIndex: 2000,
            padding: '0.5rem 0',
          }}>
            <Link href="/profile" legacyBehavior>
              <a style={{
                display: 'block',
                padding: '0.75rem 1.5rem',
                color: '#cbd5e1',
                textDecoration: 'none',
                fontWeight: 500,
                cursor: 'pointer',
                transition: 'background 0.2s',
              }}
                onMouseOver={e => e.currentTarget.style.background = '#374151'}
                onMouseOut={e => e.currentTarget.style.background = 'transparent'}
              >
                Edit Profile
              </a>
            </Link>
            <button
              style={{
                display: 'block',
                width: '100%',
                padding: '0.75rem 1.5rem',
                color: '#f87171',
                background: 'none',
                border: 'none',
                textAlign: 'left',
                fontWeight: 500,
                cursor: 'pointer',
                transition: 'background 0.2s',
              }}
              onMouseOver={e => e.currentTarget.style.background = '#374151'}
              onMouseOut={e => e.currentTarget.style.background = 'transparent'}
              onClick={() => {
                localStorage.removeItem('token');
                window.location.href = '/auth/login';
              }}
            >
              Sign out
            </button>
          </div>
        )}
      </div>
    </nav>
  );
} 