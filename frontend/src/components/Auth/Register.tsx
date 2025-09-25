import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

interface RegisterFormData {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  userType: 'BUYER' | 'SELLER';
}

const Register: React.FC = () => {
  const [formData, setFormData] = useState<RegisterFormData>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    userType: 'BUYER'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [usernameAvailable, setUsernameAvailable] = useState<boolean | null>(null);
  const [emailAvailable, setEmailAvailable] = useState<boolean | null>(null);
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const checkUsernameAvailability = async (username: string) => {
    if (username.length < 3) return;
    
    try {
      const response = await axios.get(`/api/users/check-username?username=${username}`);
      setUsernameAvailable(response.data.available);
    } catch (err) {
      setUsernameAvailable(null);
    }
  };

  const checkEmailAvailability = async (email: string) => {
    if (!email.includes('@')) return;
    
    try {
      const response = await axios.get(`/api/users/check-email?email=${email}`);
      setEmailAvailable(response.data.available);
    } catch (err) {
      setEmailAvailable(null);
    }
  };

  const handleUsernameBlur = () => {
    if (formData.username) {
      checkUsernameAvailability(formData.username);
    }
  };

  const handleEmailBlur = () => {
    if (formData.email) {
      checkEmailAvailability(formData.email);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Client-side validation
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      setLoading(false);
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post('/api/users/register', formData, {
        withCredentials: true
      });

      if (response.data.success) {
        // Redirect to home page on successful registration
        navigate('/');
        // Refresh the page to update user state
        window.location.reload();
      } else {
        setError(response.data.error || 'Registration failed');
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '500px', margin: '50px auto', padding: '20px' }}>
      <h2>Create Your eBay Account</h2>
      
      {error && (
        <div style={{ color: 'red', marginBottom: '15px', padding: '10px', backgroundColor: '#ffe6e6', borderRadius: '4px' }}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'flex', gap: '15px', marginBottom: '15px' }}>
          <div style={{ flex: 1 }}>
            <label htmlFor="firstName">First Name:</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '10px',
                marginTop: '5px',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>
          <div style={{ flex: 1 }}>
            <label htmlFor="lastName">Last Name:</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '10px',
                marginTop: '5px',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            name="username"
            value={formData.username}
            onChange={handleChange}
            onBlur={handleUsernameBlur}
            required
            style={{
              width: '100%',
              padding: '10px',
              marginTop: '5px',
              border: `1px solid ${usernameAvailable === false ? 'red' : usernameAvailable === true ? 'green' : '#ddd'}`,
              borderRadius: '4px'
            }}
          />
          {usernameAvailable === false && (
            <small style={{ color: 'red' }}>Username is already taken</small>
          )}
          {usernameAvailable === true && (
            <small style={{ color: 'green' }}>Username is available</small>
          )}
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            onBlur={handleEmailBlur}
            required
            style={{
              width: '100%',
              padding: '10px',
              marginTop: '5px',
              border: `1px solid ${emailAvailable === false ? 'red' : emailAvailable === true ? 'green' : '#ddd'}`,
              borderRadius: '4px'
            }}
          />
          {emailAvailable === false && (
            <small style={{ color: 'red' }}>Email is already registered</small>
          )}
          {emailAvailable === true && (
            <small style={{ color: 'green' }}>Email is available</small>
          )}
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            style={{
              width: '100%',
              padding: '10px',
              marginTop: '5px',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          />
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="confirmPassword">Confirm Password:</label>
          <input
            type="password"
            id="confirmPassword"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            required
            style={{
              width: '100%',
              padding: '10px',
              marginTop: '5px',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          />
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="userType">Account Type:</label>
          <select
            id="userType"
            name="userType"
            value={formData.userType}
            onChange={handleChange}
            required
            style={{
              width: '100%',
              padding: '10px',
              marginTop: '5px',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          >
            <option value="BUYER">Buyer</option>
            <option value="SELLER">Seller</option>
          </select>
        </div>

        <button
          type="submit"
          disabled={loading || usernameAvailable === false || emailAvailable === false}
          style={{
            width: '100%',
            padding: '12px',
            backgroundColor: loading || usernameAvailable === false || emailAvailable === false ? '#ccc' : '#0066cc',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            fontSize: '16px',
            cursor: loading || usernameAvailable === false || emailAvailable === false ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Creating Account...' : 'Create Account'}
        </button>
      </form>

      <div style={{ marginTop: '20px', textAlign: 'center' }}>
        <p>
          Already have an account?{' '}
          <a href="/login" style={{ color: '#0066cc', textDecoration: 'none' }}>
            Login here
          </a>
        </p>
      </div>
    </div>
  );
};

export default Register;