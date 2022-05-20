const boris = (orderNumber, surname) => {
    fetch(`http://boris.nginx-ingress.backend.k8s.decaf.prod.us-east-1.hcom.cloud/booking/${orderNumber}?surname=${surname}`,
        {
            method: 'GET', headers: {
                Accept: 'application/json',
                'Client-Version': '11.1.107',
                'Consumer-Name': 'BERPS',
                'Consumer-Version': '1.1.390',
                'Cookie': '$Version=1;boris=debugModeEnabled: false',
                'Request-Context-Brand': 'hotels.com',
                'Request-Context-Locale': 'en_GB',
                'Request-Context-Point-Of-Sale': 'HCOM_UK',
                'Request-Id': '5969af6a-0266-4ad8-a4ba-3a2bc9c1c35b'
            }
        })
        .then(res => res.json())
        .then(console.log)
        .catch(console.error);
};

.then(body => body.hotel.roomConfirmations.flatMap(rc => rc.bookingPlans.flatMap(bp => bp.deposit.textId)))

