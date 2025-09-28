import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

interface Category {
  categoryId: number;
  name: string;
  description: string;
}

interface ProductFormData {
  title: string;
  description: string;
  categoryId: number;
  condition: 'NEW' | 'LIKE_NEW' | 'VERY_GOOD' | 'GOOD' | 'ACCEPTABLE' | 'FOR_PARTS';
  listingType: 'AUCTION' | 'BUY_NOW' | 'BOTH';
  startingPrice: number;
  buyNowPrice?: number;
  reservePrice?: number;
  auctionDuration: number; // in days
  quantity: number;
  shippingCost: number;
  location: string;
}

const CreateProduct: React.FC = () => {
  const [formData, setFormData] = useState<ProductFormData>({
    title: '',
    description: '',
    categoryId: 0,
    condition: 'NEW',
    listingType: 'AUCTION',
    startingPrice: 0.99,
    auctionDuration: 7,
    quantity: 1,
    shippingCost: 0,
    location: ''
  });
  
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedImages, setSelectedImages] = useState<File[]>([]);
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);
  const [uploadingImages, setUploadingImages] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await api.get('/products/categories');
      if (response.data.success) {
        setCategories(response.data.categories);
      }
    } catch (err) {
      console.error('Failed to fetch categories:', err);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseFloat(value) || 0 : value
    }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    if (files.length > 5) {
      setError('Maximum 5 images allowed');
      return;
    }

    // Validate file types and sizes
    const validFiles = files.filter(file => {
      if (!file.type.startsWith('image/')) {
        setError('Only image files are allowed');
        return false;
      }
      if (file.size > 5 * 1024 * 1024) { // 5MB limit
        setError('Image size must be less than 5MB');
        return false;
      }
      return true;
    });

    setSelectedImages(validFiles);

    // Create image previews
    const previewUrls: string[] = [];
    validFiles.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        previewUrls.push(e.target?.result as string);
        if (previewUrls.length === validFiles.length) {
          setImagePreviews([...previewUrls]);
        }
      };
      reader.readAsDataURL(file);
    });
  };

  const removeImage = (index: number) => {
    const newFiles = selectedImages.filter((_, i) => i !== index);
    const newPreviews = imagePreviews.filter((_, i) => i !== index);
    setSelectedImages(newFiles);
    setImagePreviews(newPreviews);
  };

  const uploadImages = async (productId: number) => {
    if (selectedImages.length === 0) return [];

    setUploadingImages(true);
    try {
      const formData = new FormData();
      selectedImages.forEach(file => {
        formData.append('images', file);
      });

      const response = await api.post(`/products/${productId}/images`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        withCredentials: true
      });

      if (response.data.success) {
        return response.data.imageUrls;
      } else {
        throw new Error(response.data.error || 'Failed to upload images');
      }
    } catch (error: any) {
      console.error('Image upload error:', error);
      throw new Error(error.response?.data?.error || 'Failed to upload images');
    } finally {
      setUploadingImages(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Validate required fields
      if (!formData.title || !formData.description || !formData.categoryId) {
        setError('Please fill in all required fields');
        return;
      }

      // Calculate end time based on duration
      const startTime = new Date();
      const endTime = new Date(startTime.getTime() + (formData.auctionDuration * 24 * 60 * 60 * 1000));

      const productData = {
        ...formData,
        currentPrice: formData.startingPrice,
        startTime: startTime.toISOString(),
        endTime: formData.listingType !== 'BUY_NOW' ? endTime.toISOString() : null,
        status: 'ACTIVE'
      };

      const response = await api.post('/products', productData, {
        withCredentials: true
      });

      if (response.data.success) {
        const createdProduct = response.data.product;
        
        // Upload images if any are selected
        if (selectedImages.length > 0) {
          try {
            await uploadImages(createdProduct.productId);
          } catch (imageError: any) {
            // Product was created but image upload failed
            setError(`Product created successfully, but image upload failed: ${imageError.message}`);
            setTimeout(() => navigate('/my-ebay'), 3000); // Navigate after showing error
            return;
          }
        }
        
        navigate('/my-ebay');
      } else {
        setError(response.data.error || 'Failed to create product');
      }
    } catch (err: any) {
      console.error('Product creation error:', err);
      setError(err.response?.data?.error || 'Failed to create product. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '20px auto', padding: '20px' }}>
      <h1>List Your Item</h1>
      
      {error && (
        <div style={{ color: 'red', marginBottom: '20px', padding: '10px', backgroundColor: '#ffe6e6', borderRadius: '4px' }}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        {/* Basic Information */}
        <div style={{ marginBottom: '30px' }}>
          <h3>Basic Information</h3>
          
          <div style={{ marginBottom: '15px' }}>
            <label htmlFor="title">Title *</label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '10px',
                marginTop: '5px',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
              placeholder="Enter a descriptive title for your item"
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label htmlFor="description">Description *</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              required
              rows={5}
              style={{
                width: '100%',
                padding: '10px',
                marginTop: '5px',
                border: '1px solid #ddd',
                borderRadius: '4px',
                resize: 'vertical'
              }}
              placeholder="Describe your item in detail"
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
            <div>
              <label htmlFor="categoryId">Category *</label>
              <select
                id="categoryId"
                name="categoryId"
                value={formData.categoryId}
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
                <option value={0}>Select a category</option>
                {categories.map(category => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="condition">Condition</label>
              <select
                id="condition"
                name="condition"
                value={formData.condition}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '10px',
                  marginTop: '5px',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
              >
                <option value="NEW">New</option>
                <option value="LIKE_NEW">Like New</option>
                <option value="VERY_GOOD">Very Good</option>
                <option value="GOOD">Good</option>
                <option value="ACCEPTABLE">Acceptable</option>
                <option value="FOR_PARTS">For Parts/Not Working</option>
              </select>
            </div>
          </div>
        </div>

        {/* Selling Format */}
        <div style={{ marginBottom: '30px' }}>
          <h3>Selling Format</h3>
          
          <div style={{ marginBottom: '15px' }}>
            <label htmlFor="listingType">Listing Type</label>
            <select
              id="listingType"
              name="listingType"
              value={formData.listingType}
              onChange={handleChange}
              style={{
                width: '100%',
                padding: '10px',
                marginTop: '5px',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value="AUCTION">Auction</option>
              <option value="BUY_NOW">Buy It Now</option>
              <option value="BOTH">Auction with Buy It Now</option>
            </select>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '15px' }}>
            <div>
              <label htmlFor="startingPrice">
                {formData.listingType === 'BUY_NOW' ? 'Price' : 'Starting Price'} ($)
              </label>
              <input
                type="number"
                id="startingPrice"
                name="startingPrice"
                value={formData.startingPrice}
                onChange={handleChange}
                min="0.01"
                step="0.01"
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

            {formData.listingType === 'BOTH' && (
              <div>
                <label htmlFor="buyNowPrice">Buy It Now Price ($)</label>
                <input
                  type="number"
                  id="buyNowPrice"
                  name="buyNowPrice"
                  value={formData.buyNowPrice || ''}
                  onChange={handleChange}
                  min="0.01"
                  step="0.01"
                  style={{
                    width: '100%',
                    padding: '10px',
                    marginTop: '5px',
                    border: '1px solid #ddd',
                    borderRadius: '4px'
                  }}
                />
              </div>
            )}

            {formData.listingType !== 'BUY_NOW' && (
              <div>
                <label htmlFor="auctionDuration">Auction Duration (days)</label>
                <select
                  id="auctionDuration"
                  name="auctionDuration"
                  value={formData.auctionDuration}
                  onChange={handleChange}
                  style={{
                    width: '100%',
                    padding: '10px',
                    marginTop: '5px',
                    border: '1px solid #ddd',
                    borderRadius: '4px'
                  }}
                >
                  <option value={1}>1 day</option>
                  <option value={3}>3 days</option>
                  <option value={5}>5 days</option>
                  <option value={7}>7 days</option>
                  <option value={10}>10 days</option>
                </select>
              </div>
            )}
          </div>
        </div>

        {/* Item Details */}
        <div style={{ marginBottom: '30px' }}>
          <h3>Item Details</h3>
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '15px' }}>
            <div>
              <label htmlFor="quantity">Quantity</label>
              <input
                type="number"
                id="quantity"
                name="quantity"
                value={formData.quantity}
                onChange={handleChange}
                min="1"
                style={{
                  width: '100%',
                  padding: '10px',
                  marginTop: '5px',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
              />
            </div>

            <div>
              <label htmlFor="shippingCost">Shipping Cost ($)</label>
              <input
                type="number"
                id="shippingCost"
                name="shippingCost"
                value={formData.shippingCost}
                onChange={handleChange}
                min="0"
                step="0.01"
                style={{
                  width: '100%',
                  padding: '10px',
                  marginTop: '5px',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
              />
            </div>

            <div>
              <label htmlFor="location">Location</label>
              <input
                type="text"
                id="location"
                name="location"
                value={formData.location}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '10px',
                  marginTop: '5px',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
                placeholder="City, State"
              />
            </div>
          </div>
        </div>

        {/* Image Upload */}
        <div style={{ marginBottom: '30px' }}>
          <h3>Product Images</h3>
          
          <div style={{ marginBottom: '15px' }}>
            <label htmlFor="images">Upload Images (Optional)</label>
            <input
              type="file"
              id="images"
              name="images"
              multiple
              accept="image/*"
              onChange={handleImageChange}
              style={{
                width: '100%',
                padding: '10px',
                marginTop: '5px',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
            <div style={{ fontSize: '12px', color: '#666', marginTop: '5px' }}>
              Maximum 5 images, 5MB each. Supported formats: JPG, PNG, GIF, WebP
            </div>
          </div>

          {/* Image Previews */}
          {imagePreviews.length > 0 && (
            <div style={{ marginTop: '15px' }}>
              <h4>Image Previews:</h4>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(120px, 1fr))', gap: '10px' }}>
                {imagePreviews.map((preview, index) => (
                  <div key={index} style={{ position: 'relative' }}>
                    <img
                      src={preview}
                      alt={`Preview ${index + 1}`}
                      style={{
                        width: '100%',
                        height: '120px',
                        objectFit: 'cover',
                        borderRadius: '4px',
                        border: '1px solid #ddd'
                      }}
                    />
                    <button
                      type="button"
                      onClick={() => removeImage(index)}
                      style={{
                        position: 'absolute',
                        top: '5px',
                        right: '5px',
                        backgroundColor: 'rgba(255, 0, 0, 0.8)',
                        color: 'white',
                        border: 'none',
                        borderRadius: '50%',
                        width: '25px',
                        height: '25px',
                        cursor: 'pointer',
                        fontSize: '12px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                    >
                      ×
                    </button>
                    {index === 0 && (
                      <div style={{
                        position: 'absolute',
                        bottom: '5px',
                        left: '5px',
                        backgroundColor: 'rgba(0, 102, 204, 0.8)',
                        color: 'white',
                        padding: '2px 6px',
                        borderRadius: '3px',
                        fontSize: '10px'
                      }}>
                        Primary
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Submit Button */}
        <div style={{ textAlign: 'center', marginTop: '30px' }}>
          <button
            type="submit"
            disabled={loading || uploadingImages}
            style={{
              padding: '12px 40px',
              backgroundColor: (loading || uploadingImages) ? '#ccc' : '#0066cc',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              fontSize: '16px',
              cursor: (loading || uploadingImages) ? 'not-allowed' : 'pointer',
              marginRight: '10px'
            }}
          >
            {uploadingImages ? 'Uploading Images...' : loading ? 'Creating Listing...' : 'List Item'}
          </button>
          
          <button
            type="button"
            onClick={() => navigate('/my-ebay')}
            style={{
              padding: '12px 40px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              fontSize: '16px',
              cursor: 'pointer'
            }}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateProduct;