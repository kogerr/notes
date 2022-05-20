const getStatus = response => {
    const current = response.parcellifecycleResponse.parcelLifeCycleData.statusInfo.find(status => status.isCurrentStatus);
    const status = { date: current.date, location: current.location };
    current.description.content.forEach((e, i) => status[i] = e);

    return status;
};

const getScanInfo = response => {
    const scans = response.parcellifecycleResponse.parcelLifeCycleData.scanInfo.scan;
    const current = scans[scans.length - 1];
    const scanInfo = { date: current.date, name: current.scanData.scanType.name };
    current.scanDescription.content.forEach((e, i) => scanInfo[i] = e);

    return scanInfo;
};

fetch('https://tracking.dpd.de/rest/plc/en_US/15976848631302').then(res => res.json()).then(res => [ getStatus(res), getScanInfo(res) ]).then(results => results.forEach(console.log)).catch(console.error);
