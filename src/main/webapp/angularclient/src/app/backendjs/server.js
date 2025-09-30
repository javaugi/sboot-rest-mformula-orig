// server.js  Backend Application (Node.js/Express)
const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// Sample product data
const products = {
    coffeeMugs: {
        id: 'coffee-mugs',
        name: 'Coffee Mugs',
        category: 'mugs',
        variants: {
            sizes: ['8oz', '12oz', '16oz'],
            colors: ['Red', 'Green', 'White', 'Black']
        },
        basePrice: 12.99,
        description: 'High-quality ceramic coffee mugs'
    },
    tShirts: {
        id: 't-shirts',
        name: 'T-Shirts',
        category: 'clothing',
        variants: {
            types: ['Men', 'Women', 'Boys', 'Girls'],
            sizes: ['S', 'M', 'L'],
            colors: ['Pink', 'Orange', 'Blue']
        },
        basePrice: 19.99,
        description: 'Comfortable cotton t-shirts'
    },
    soccerBalls: {
        id: 'soccer-balls',
        name: 'Soccer Balls',
        category: 'sports',
        variants: {
            colors: ['Green', 'Orange', 'Purple']
        },
        basePrice: 24.99,
        description: 'Professional quality soccer balls'
    }
};

// Shopping cart (in-memory storage for demo)
let cart = [];

// Routes

// Get all products
app.get('/api/products', (req, res) => {
    res.json(products);
});

// Add item to cart
app.post('/api/cart/add', (req, res) => {
    const {productId, variant, quantity = 1} = req.body;

    const product = products[productId];
    if (!product) {
        return res.status(404).json({error: 'Product not found'});
    }

    // Calculate price based on variants
    let price = product.basePrice;

    // Add to cart
    const cartItem = {
        id: Date.now().toString(),
        productId,
        productName: product.name,
        variant,
        quantity: parseInt(quantity),
        price,
        total: price * parseInt(quantity),
        timestamp: new Date().toISOString()
    };

    cart.push(cartItem);
    res.json({success: true, cartItem, cartCount: cart.length});
});

// Get cart items
app.get('/api/cart', (req, res) => {
    res.json(cart);
});

// Update cart item quantity
app.put('/api/cart/update/:itemId', (req, res) => {
    const {itemId} = req.params;
    const {quantity} = req.body;

    const itemIndex = cart.findIndex(item => item.id === itemId);
    if (itemIndex === -1) {
        return res.status(404).json({error: 'Item not found in cart'});
    }

    cart[itemIndex].quantity = parseInt(quantity);
    cart[itemIndex].total = cart[itemIndex].price * parseInt(quantity);

    res.json({success: true, item: cart[itemIndex]});
});

// Remove item from cart
app.delete('/api/cart/remove/:itemId', (req, res) => {
    const {itemId} = req.params;

    cart = cart.filter(item => item.id !== itemId);
    res.json({success: true, cartCount: cart.length});
});

// Checkout
app.post('/api/checkout', (req, res) => {
    const {customerInfo, paymentInfo} = req.body;

    if (cart.length === 0) {
        return res.status(400).json({error: 'Cart is empty'});
    }

    // Calculate totals
    const subtotal = cart.reduce((sum, item) => sum + item.total, 0);
    const tax = subtotal * 0.08; // 8% tax
    const shipping = 5.99;
    const total = subtotal + tax + shipping;

    // Create order (in a real app, you'd save to database)
    const order = {
        orderId: 'ORD' + Date.now(),
        items: [...cart],
        customerInfo,
        paymentInfo: {...paymentInfo, cardNumber: '****' + paymentInfo.cardNumber.slice(-4)},
        subtotal: subtotal.toFixed(2),
        tax: tax.toFixed(2),
        shipping: shipping.toFixed(2),
        total: total.toFixed(2),
        orderDate: new Date().toISOString()
    };

    // Clear cart after successful order
    cart = [];

    res.json({
        success: true,
        order,
        message: 'Order placed successfully!'
    });
});

// Clear cart
app.delete('/api/cart/clear', (req, res) => {
    cart = [];
    res.json({success: true, message: 'Cart cleared'});
});

// Serve frontend
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
