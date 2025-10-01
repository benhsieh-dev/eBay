package com.ebay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GraphiQLController {

    @GetMapping("/graphiql")
    @ResponseBody
    public String graphiql() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>GraphQL Playground</title>
            <style>
                body { 
                    margin: 0; 
                    font-family: Arial, sans-serif; 
                    background: #f5f5f5;
                    display: flex;
                    flex-direction: column;
                    height: 100vh;
                }
                .header {
                    background: #333;
                    color: white;
                    padding: 1rem;
                    text-align: center;
                }
                .container {
                    display: flex;
                    flex: 1;
                    gap: 1rem;
                    padding: 1rem;
                }
                .query-panel, .result-panel {
                    flex: 1;
                    background: white;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                }
                textarea {
                    width: 100%;
                    height: 300px;
                    border: none;
                    padding: 1rem;
                    font-family: monospace;
                    resize: vertical;
                }
                .result {
                    padding: 1rem;
                    font-family: monospace;
                    background: #f9f9f9;
                    white-space: pre-wrap;
                    height: 300px;
                    overflow: auto;
                }
                button {
                    background: #007acc;
                    color: white;
                    border: none;
                    padding: 0.5rem 1rem;
                    margin: 1rem;
                    cursor: pointer;
                    border-radius: 4px;
                }
                button:hover {
                    background: #005999;
                }
                .examples {
                    padding: 1rem;
                    background: white;
                    margin: 1rem;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                }
                .example {
                    margin: 0.5rem 0;
                    padding: 0.5rem;
                    background: #f0f0f0;
                    cursor: pointer;
                    border-radius: 3px;
                }
                .example:hover {
                    background: #e0e0e0;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>GraphQL API Playground</h1>
                <p>eBay Marketplace GraphQL Endpoint</p>
            </div>
            
            <div class="examples">
                <strong>Quick Examples (click to use):</strong>
                <div class="example" onclick="setQuery(examples.products)">📦 Get all products</div>
                <div class="example" onclick="setQuery(examples.product)">🎯 Get single product</div>
                <div class="example" onclick="setQuery(examples.bid)">💰 Place a bid (requires login)</div>
            </div>
            
            <div class="container">
                <div class="query-panel">
                    <h3 style="margin: 1rem;">GraphQL Query:</h3>
                    <textarea id="query" placeholder="Enter your GraphQL query here...">
{
  products {
    productId
    title
    currentPrice
    seller {
      username
      firstName
    }
  }
}</textarea>
                    <button onclick="executeQuery()">▶ Execute Query</button>
                </div>
                
                <div class="result-panel">
                    <h3 style="margin: 1rem;">Result:</h3>
                    <div id="result" class="result">Click "Execute Query" to see results...</div>
                </div>
            </div>
            
            <script>
                const examples = {
                    products: `{
  products {
    productId
    title
    currentPrice
    seller {
      username
      firstName
    }
    category {
      categoryName
    }
  }
}`,
                    product: `{
  product(id: "1") {
    productId
    title
    description
    currentPrice
    seller {
      username
    }
    category {
      categoryName
    }
  }
}`,
                    bid: `mutation {
  placeBid(productId: "1", price: 5.0) {
    success
    message
    bidId
  }
}`
                };
                
                function setQuery(query) {
                    document.getElementById('query').value = query;
                }
                
                async function executeQuery() {
                    const query = document.getElementById('query').value;
                    const resultDiv = document.getElementById('result');
                    
                    if (!query.trim()) {
                        resultDiv.textContent = 'Please enter a query';
                        return;
                    }
                    
                    resultDiv.textContent = 'Executing...';
                    
                    try {
                        const response = await fetch('/graphql', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify({ query: query })
                        });
                        
                        const result = await response.json();
                        resultDiv.textContent = JSON.stringify(result, null, 2);
                    } catch (error) {
                        resultDiv.textContent = 'Error: ' + error.message;
                    }
                }
                
                // Execute default query on load
                window.onload = function() {
                    executeQuery();
                };
            </script>
        </body>
        </html>
        """;
    }
}