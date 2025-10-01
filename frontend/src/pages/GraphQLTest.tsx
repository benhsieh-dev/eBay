import React, { useState } from 'react';

const GraphQLTest: React.FC = () => {
    const [query, setQuery] = useState(`{
  products(limit: 3) {
    productId
    title
    currentPrice
    seller {
      username
    }
  }
}`);
    const [result, setResult] = useState<string>('');
    const [loading, setLoading] = useState(false);

    const executeQuery = async () => {
        setLoading(true);
        try {
            const response = await fetch('/graphql', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ query })
            });
            
            const data = await response.json();
            setResult(JSON.stringify(data, null, 2));
        } catch (error) {
            setResult(`Error: ${error}`);
        } finally {
            setLoading(false);
        }
    };

    const testQueries = {
        products: `{
  products(limit: 5) {
    productId
    title
    currentPrice
    seller {
      username
    }
  }
}`,
        singleProduct: `{
  product(id: "1") {
    productId
    title
    currentPrice
  }
}`,
        categoryFilter: `{
  products(limit: 3, category: "Electronics") {
    productId
    title
    currentPrice
  }
}`,
        placeBid: `mutation {
  placeBid(productId: "1", price: 25.50) {
    success
    message
    bidId
  }
}`
    };

    return (
        <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
            <h1>GraphQL Test Interface</h1>
            
            <div style={{ marginBottom: '20px' }}>
                <h3>Quick Test Queries:</h3>
                {Object.entries(testQueries).map(([name, testQuery]) => (
                    <button
                        key={name}
                        onClick={() => setQuery(testQuery)}
                        style={{ margin: '5px', padding: '8px 12px' }}
                    >
                        {name}
                    </button>
                ))}
            </div>

            <div style={{ display: 'flex', gap: '20px', height: '500px' }}>
                <div style={{ flex: 1 }}>
                    <h3>GraphQL Query:</h3>
                    <textarea
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        style={{ 
                            width: '100%', 
                            height: '300px', 
                            fontFamily: 'monospace',
                            padding: '10px'
                        }}
                        placeholder="Enter your GraphQL query here..."
                    />
                    <br />
                    <button 
                        onClick={executeQuery} 
                        disabled={loading}
                        style={{ 
                            padding: '10px 20px', 
                            marginTop: '10px',
                            backgroundColor: '#007bff',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: loading ? 'not-allowed' : 'pointer'
                        }}
                    >
                        {loading ? 'Executing...' : 'Execute Query'}
                    </button>
                </div>

                <div style={{ flex: 1 }}>
                    <h3>Response:</h3>
                    <pre style={{ 
                        backgroundColor: '#f8f9fa', 
                        padding: '15px', 
                        border: '1px solid #dee2e6',
                        borderRadius: '4px',
                        height: '350px',
                        overflow: 'auto',
                        fontSize: '12px'
                    }}>
                        {result || 'No response yet. Click "Execute Query" to test.'}
                    </pre>
                </div>
            </div>

            <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#e9ecef', borderRadius: '4px' }}>
                <h4>Instructions:</h4>
                <ol>
                    <li>Click on a quick test button to load a sample query</li>
                    <li>Or write your own GraphQL query in the text area</li>
                    <li>Click "Execute Query" to test</li>
                    <li>View the response on the right</li>
                </ol>
                <p><strong>Note:</strong> For bid mutations, make sure you're logged in first!</p>
            </div>
        </div>
    );
};

export default GraphQLTest;