const https = require('https');

const ignored = [
    {"locale":"en_US","brand":"delta","pointOfSale":"DELTA_US","customerLastName":"Collins","orderNumber":"9203333205890","emailAddresses":"gekovacs@hotels.com","emailOrigin":"T_TR_CONF_RESEND_VRP","emailType":"orderConfirmationEmail"}
];


// const hostname = 'berqfs2.istio-gateway.backend.k8s.us-west-2.hcom-lab-decaf.aws.hcom';
// const path = '/send/email';
const hostname = 'www.hotels.com';
const path = '/resend/mail.html';
const method = 'POST';

const send = (body, headers, cb) => {
    const options = { hostname, path, method, headers, body };
    const req = https.request(options, (res) => {
        console.log(res.statusCode);
        // console.log(`HEADERS: ${JSON.stringify(res.headers)}`);
        res.setEncoding('utf8');
        res.on('data', (chunk) => {
            // console.log(`BODY: ${chunk}`);
        });
        res.on('end', () => {
            cb();
            // console.log('No more data in response.');
        });
    });

    req.on('error', (e) => {
        console.error(`problem with request: ${e.message}`);
        cb();
    });

    req.write(JSON.stringify(body));
    req.end();
}

// for (let i = 0; i < ignored.length; i++) {
//     const body = { emailType: ignored[i].emailType, order: ignored[i].order, recipients: ignored[i].recipients, metaData: ignored[i].metaData };
//     const headers = { 'Content-Type': 'application/json', 'Request-Context-Brand': ignored[i].brand, 'Request-Context-Client-Id': 'test', 'Request-Context-Locale': ignored[i].locale, 'Request-Context-Point-Of-Sale': ignored[i].pointOfSale, 'Request-Context-Request-Id': 'ebbddc0b-202d-49fa-89b1-' + i };
//     send(body, headers);
// }

const sendRequests = (i) => {
    console.log(i);
    const emailType = ignored[i].emailType;
    const metaData = { emailOrigin: ignored[i].emailOrigin };
    const order = { customerLastName: ignored[i].customerLastName, orderNumber: ignored[i].orderNumber };
    const recipients = [{ emailAddress: ignored[i].emailAddresses }];
    const body = { emailType, metaData, order, recipients };
    const pointOfSale = ignored[i].pointOfSale.length > 0 ? ignored[i].pointOfSale : 'HCOM_US';
    const requestId = 'ebbddc0b-202d-49fa-89b2-' + i.toString(10).padStart(12, '0');
    const headers = { 'Content-Type': 'application/json', 'Request-Context-Brand': ignored[i].brand, 'Request-Context-Client-Id': 'test', 'Request-Context-Locale': ignored[i].locale, 'Request-Context-Point-Of-Sale': pointOfSale, 'Request-Context-Request-Id': requestId };
    if (i < ignored.length) {
        send(body, headers, () => sendRequests(i + 1));
    }
};

const bulkSend = (i) => {
    console.log(i);
    const body = {
        "emailType": "orderConfirmationEmail",
        "metaData": {
            "emailOrigin": "T_TR_CONF_INSTANT"
        },
        "order": {
            "customerLastName": "Stenfeldt-Nilsen",
            "orderNumber": 9199522108497
        },
        "recipients": [
            {
                "emailAddress": "gekovacs@hotels.com"
            }
        ]
    };
    const requestId = ('123e4567-e89b-12d3-a456-426655440' + i).padEnd(36, '0');
    const headers = { 'Content-Type': 'application/json', 'Request-Context-Brand': 'hotels.com', 'Request-Context-Client-Id': 'test', 'Request-Context-Locale': 'no_NO', 'Request-Context-Point-Of-Sale': 'HCOM_NO', 'Request-Context-Request-Id': requestId };
    if (i < 1000) {
        send(body, headers, () => setTimeout(() => bulkSend(i + 1), Math.floor(Math.random() * 30000)));
    }
};

// sendRequests(0);

bulkSend(0);

// const headers = { 'Content-Type': 'application/json', 'Request-Context-Brand': 'hotels.com', 'Request-Context-Client-Id': 'test', 'Request-Context-Locale': 'en_US', 'Request-Context-Point-Of-Sale': 'HCOM_US', 'Request-Context-Request-Id': 'ebbddc0b-202d-49fa-89b1-f77fa0690bc5' };
// const body = {"emailType":"orderConfirmationEmail", order: { "customerLastName":"Doe","orderNumber":9025035358696 }, recipients: [ {"emailAddress":"gekovacs@hotels.com"} ], metaData: { "emailOrigin":"T_TR_CONF_RESEND_VRP" } }
