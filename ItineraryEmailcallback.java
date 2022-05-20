/**
 * ItineraryDetailsEmailCallbackController.java
 *
 * Copyright 2012 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.expedia.www.itin.ui.controllers;

import com.expedia.e3.platform.foundation.configuration.BizConfig;
import com.expedia.e3.platform.foundation.configuration.IStripingContext;
import com.expedia.e3.platform.foundation.core.util.GUID;
import com.expedia.e3.platform.siteid.locale.translator.ILocaleLangIdTranslator;
import com.expedia.www.domain.api.identity.IIdentityDomain;
import com.expedia.www.domain.config.CustomerCareConfig;
import com.expedia.www.domain.config.ItineraryDetailsPWPConfiguration;
import com.expedia.www.domain.config.ItineraryDetailsValueConfiguration;
import com.expedia.www.domain.config.checkout.LobStripingContext;
import com.expedia.www.domain.config.partner.PartnerPointsConfig;
import com.expedia.www.domain.entities.IRequestContext;
import com.expedia.www.domain.entities.flight.domain.ConfirmationCode;
import com.expedia.www.domain.entities.flight.domain.IFlightLeg;
import com.expedia.www.domain.entities.flight.domain.IFlightProduct;
import com.expedia.www.domain.entities.flight.domain.IFlightSegment;
import com.expedia.www.domain.entities.flight.domain.IFlightTrip;
import com.expedia.www.domain.entities.flight.domain.carrierdetails.IAirCarrierDetails;
import com.expedia.www.domain.entities.impl.User;
import com.expedia.www.domain.entities.trip.IFlightComponent;
import com.expedia.www.domain.entities.trip.ITrip;
import com.expedia.www.domain.entities.user.IUser;
import com.expedia.www.domain.presentation.SiteBrandModelBuilder;
import com.expedia.www.domain.services.checkout.ICouponService;
import com.expedia.www.domain.services.common.config.ICouponConfig;
import com.expedia.www.domain.services.flight.airline.IAirCarrierDetailsService;
import com.expedia.www.domain.services.hotel.config.IItineraryLocalizationSettings;
import com.expedia.www.domain.services.notification.ProductTypeIdMapper;
import com.expedia.www.domain.services.selfserv.CancelActionEnum;
import com.expedia.www.domain.services.selfserv.ExchangeActionEnum;
import com.expedia.www.domain.services.selfserv.IAirChangeService;
import com.expedia.www.domain.services.selfserv.IAirPenaltyInfo;
import com.expedia.www.domain.services.selfserv.ItineraryLogger;
import com.expedia.www.domain.services.user.token.IUserTokenGenerateService;
import com.expedia.www.domain.trip.exception.TripUserValidationException;
import com.expedia.www.domain.trip.model.Component;
import com.expedia.www.domain.trip.model.customersupport.CustomerCareCustomerType;
import com.expedia.www.domain.trip.model.SimplifiedTrip;
import com.expedia.www.domain.trip.model.flight.Flight;
import com.expedia.www.domain.trip.service.ISimplifiedTripService;
import com.expedia.www.domain.trip.support.ItinMetricsEnum;
import com.expedia.www.domain.trip.support.TripIDType;
import com.expedia.www.domain.util.EmailLinkBaseUrl;
import com.expedia.www.domain.util.LoyaltyConstants;
import com.expedia.www.domain.util.loyalty.LoyaltyBrandUtil;
import com.expedia.www.domain.util.partnerloyalty.IBankLoyaltyCreditCardService;
import com.expedia.www.domain.utils.FormatsApiHelper;
import com.expedia.www.domain.valuetypes.DateRange;
import com.expedia.www.domain.valuetypes.IPrice;
import com.expedia.www.domain.valuetypes.LocalizedDateTime;
import com.expedia.www.domain.valuetypes.LocalizedPrice;
import com.expedia.www.domain.valuetypes.Name;
import com.expedia.www.domain.valuetypes.ProductType;
import com.expedia.www.domain.valuetypes.hotel.AbacusInfo;
import com.expedia.www.globalcontrols.core.Environment;
import com.expedia.www.globalcontrols.core.io.configuration.EnvironmentVersionType;
import com.expedia.www.itin.ui.common.FlightTicketingUtil;
import com.expedia.www.itin.ui.common.ItineraryEmailPrintPageConfig;
import com.expedia.www.itin.ui.common.JsonEmailCallbackResponse;
import com.expedia.www.itin.ui.common.TripPageUtilities;
import com.expedia.www.itin.ui.common.UrlUtils;
import com.expedia.www.itin.ui.config.ItineraryDetailsFeatureSettingsConfiguration;
import com.expedia.www.itin.ui.config.ItineraryDetailsInsuranceConfig;
import com.expedia.www.itin.ui.config.ItineraryMessageConfiguration;
import com.expedia.www.itin.ui.config.ResponsiveLOBStripingContext;
import com.expedia.www.itin.ui.controllers.actions.ShowItinDetailsAction;
import com.expedia.www.itin.ui.controllers.email.OptionalNotificationEmailData;
import com.expedia.www.itin.ui.exceptions.EmailHtmlGenerationException;
import com.expedia.www.itin.ui.modelbuilders.InsurancePostPurchaseCrossSellFactory;
import com.expedia.www.itin.ui.modelbuilders.ItineraryCorePageModelBuilder;
import com.expedia.www.itin.ui.modelbuilders.RailModelBuilder;
import com.expedia.www.itin.ui.modelbuilders.legal.ElevyModel;
import com.expedia.www.itin.ui.modelbuilders.legal.ElevyModelBuilder;
import com.expedia.www.itin.ui.repositories.ITripRepository;
import com.expedia.www.itin.ui.responsive.MessageHotelEmailUrlBuilder;
import com.expedia.www.itin.ui.responsive.ResponsiveAbacusHelper;
import com.expedia.www.itin.ui.responsive.TripDetailEmailCallbackController;
import com.expedia.www.itin.ui.responsive.modelbuilder.web.InsuranceUIControlUtil;
import com.expedia.www.itin.ui.uimodel.FlightModel;
import com.expedia.www.itin.ui.uimodel.FlightTravelerUIModel;
import com.expedia.www.itin.ui.uimodel.HotelModel;
import com.expedia.www.itin.ui.uimodel.ItineraryCorePageModel;
import com.expedia.www.itin.ui.uimodel.PackageModel;
import com.expedia.www.itin.ui.uimodel.email.DCTKData;
import com.expedia.www.itin.ui.uimodel.xsell.InsurancePostPurchaseCrossSell;
import com.expedia.www.itin.ui.util.ItinCWLogger;
import com.expedia.www.itin.ui.util.TripOrderProcessingUtils;
import com.expedia.www.platform.common.HttpOptions;
import com.expedia.www.platform.common.SslHandlingOption;
import com.expedia.www.platform.context.Site;
import com.expedia.www.platform.diagnostics.SystemEvent;
import com.expedia.www.platform.exppage.identity.ExpLineOfBusiness;
import com.expedia.www.platform.l10n.resource.adapter.ResourceBundleAdaptorFactorySimplifiedUISupport;
import com.expedia.www.platform.localization.SimpleSiteContextBuilder;
import com.expedia.www.platform.page.identity.FunnelLocation;
import com.expedia.www.platform.page.identity.Identity;
import com.expedia.www.platform.page.identity.PageIdentifierString;
import com.expedia.www.platform.presentation.core.common.NamesForDisplayConfig;
import com.expedia.www.platform.presentation.core.page.IdentifiablePage;
import com.expedia.www.platform.presentation.sitebrand.ui.SiteBrandModel;
import com.expedia.www.shared.ui.UiSystemEvent;
import com.expedia.www.shared.ui.configuration.ItineraryDisplayConfiguration;
import com.expedia.www.shared.ui.modelbuilders.partnerloyalty.PartnerLoyaltyConfirmationMsgModelBuilder;
import com.expedia.www.shared.ui.models.partnerloyalty.PartnerLoyaltyConfirmationMsgModel;
import com.expedia.www.shared.ui.user.email.IdentityTokenConfig;
import com.expedia.www.shared.ui.utils.EvolableHelper;
import com.expedia.www.user.exception.IdentityTokenException;
import com.expedia.www.user.identity.UserDetails;
import com.expedia.www.user.identity.impl.UserToken;
import com.expedia.www.webdomain.api.identity.IUserAuthenticator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.BooleanUtils.toBooleanDefaultIfNull;

@SuppressWarnings({"PMD.ExcessiveImports","PMD.ExcessiveMethodLength"})
@HttpOptions(sslHandling = SslHandlingOption.NON_SSL_REDIRECTS_TO_SSL, preventCaching = true)
public class ItineraryEmailCallback extends
        AbstractItineraryController implements IdentifiablePage {
    private static final ItineraryLogger LOGGER = ItineraryLogger.getLogger(ItineraryEmailCallback.class);
    // private static final String DATE_FORMAT_IBP_CONFIRMATION_EMAIL =
    // "MMM dd, yyyy";
    private static final Pattern LONG_GUID_PATTERN =
            Pattern.compile("([a-f0-9]{8})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{12})");
    private static final String PAGE_NAME = "page.Itin.Email.Callback";
    public static final String VIEW_NAME = "ftl:pages/itin/trip_details_email";
    public static final String MOBILEFRIENDLY_VIEW_NAME = "ftl:pages/itin/trip_details_single_column_email";
    protected static final String SUCCESS_STATUS = "SUCCESS";
    protected static final String ERROR_STATUS = "ERROR";
    protected static final String ITIN_CHANGE = "itinchange";
    protected static final String ITIN_UPGRADE = "itinupgrade";
    protected static final String PURCHASE_CONFIRMATION = "purchaseConfirmation";
    protected static final String ITIN = "itin";
    private static final String ELEVY = "elevy";
    private static final String TRIP_PURCHASE_CONF_EMAIL = "ftl:pages/itin/email/subject/purchase_confirmation";
    private static final String CHANGE_EMAIL_EVAR = "change.email.click";

    private Map<String, String> map = new HashMap<>();
    private UserDetails userDetails;
    private UserToken userToken;
    SecureRequestWrapperUtil secureRequestWrapperUtil = new SecureRequestWrapperUtil();
    private final EmailLinkBaseUrl emailLinkBaseUrl;

    @Autowired
    private ItinCWLogger itinCWLogger;

    @Autowired
    private TripPageUtilities tripPageUtilities;

    @Autowired
    protected ProductTypeIdMapper productTypeIDMapper;

    @Autowired
    private ItineraryDetailsHelper callbackControllerHelper;

    @Autowired
    protected ItineraryCorePageModelBuilder pageModelBuilder;

    @Autowired
    @Qualifier("com.expedia.www.itin.ui.showItinDetailsAction")
    protected ShowItinDetailsAction showItinDetailsAction;

    @Autowired
    protected ILocaleLangIdTranslator langIdToLocaleTranslator;

    @Autowired
    private EmailLinkBaseUrl emailLinkUrl;

    @Autowired
    @Qualifier("TripRepository")
    protected ITripRepository tripRepository;

    @Autowired
    @Qualifier("com.expedia.www.itin.ui.localization.config.impl.ItineraryLocalizationSettings")
    IItineraryLocalizationSettings settings;

    @BizConfig
    protected ItineraryMessageConfiguration itineraryMessageTemplate;

    @BizConfig
    private IdentityTokenConfig identityTokenconfig;

    @BizConfig
    private ItineraryDetailsFeatureSettingsConfiguration featureConfig;

    @Autowired
    private ItineraryDetailsHelper itineraryDetailsHelper;

    @Autowired
    private ResponsiveAbacusHelper responsiveAbacusHelper;

    @Autowired
    private TripDetailEmailCallbackController tripDetailEmailCallBackContorller;

    @Autowired
    protected ISimplifiedTripService simplifiedTripService;

    @Autowired
    protected IAirChangeService airChangeService;

    @Autowired(required = true)
    private IUserAuthenticator userAuthenticator;

    @Autowired
    private NamesForDisplayConfig namesForDisplayConfig;

    @Autowired
    @Qualifier("messageSourceShim")
    MessageSource messageSource;

    @Autowired
    private ItineraryEmailPrintPageConfig itineraryEmailPrintPageConfig;

    @Autowired
    protected IAirCarrierDetailsService airCarrierDetailsService;

    @Autowired
    private FormatsApiHelper formatsApiHelper;

    @Autowired
    private PartnerLoyaltyConfirmationMsgModelBuilder partnerLoyaltyConfMsgBuilder;

    @Autowired
    private PartnerPointsConfig partnerPointsConfig;

    @Autowired
    protected RailModelBuilder railModelBuilder;

    @Autowired
    ItineraryLogger itineraryLogger;

    @Autowired
    private ItineraryDetailsInsuranceConfig itineraryDetailsInsuranceConfig;

    @Autowired
    private MessageHotelEmailUrlBuilder messageHotelEmailUrlBuilder;

    @Autowired
    private EvolableHelper evolableHelper;

    @BizConfig
    private ItineraryDisplayConfiguration itineraryDisplayConfiguration;

    @BizConfig
    private ItineraryDetailsValueConfiguration itineraryDetailsValueConfiguration;

    @Autowired
    private IUserTokenGenerateService userTokenGenerateService;

    @Autowired
    private ElevyModelBuilder elevyModelBuilder;
    private IIdentityDomain identityDomain;

    @Autowired
    private UrlUtils urlUtils;

    public ItineraryEmailCallback(EmailLinkBaseUrl emailLinkBaseUrl, IIdentityDomain identityDomain) {
        super(PAGE_NAME, "Itin.email", "NA", "hotels");
        this.emailLinkBaseUrl = emailLinkBaseUrl;
        this.identityDomain = identityDomain;
    }

    public String getId() {
        return PAGE_NAME;
    }

    @Autowired
    private ICouponConfig couponConfig;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private TripOrderProcessingUtils tripOrderProcessingUtils;

    @Autowired
    private CustomerCareConfig customerCareConfig;

    @BizConfig
    private ItineraryDetailsPWPConfiguration pwpConfig;

    private static final ResourceBundleAdaptorFactorySimplifiedUISupport RESOURCE_BUNDLE_ADAPTER_FACTORY = new ResourceBundleAdaptorFactorySimplifiedUISupport(
            ClassUtils.getDefaultClassLoader());

    @Autowired
    private SiteBrandModelBuilder siteBrandModelBuilder;

    @Autowired
    IBankLoyaltyCreditCardService bankLoyaltyCreditCardService;

    @Autowired
    InsurancePostPurchaseCrossSellFactory insurancePostPurchaseXSellFactory;

    @Override
    public Identity getIdentity(HttpServletRequest request)
    {
        String pageId = getId();
        return new Identity(new PageIdentifierString(pageId), FunnelLocation.POST_BOOKING, ExpLineOfBusiness.UNCLASSIFIED);
    }

    public JsonEmailCallbackResponse getEmailContent(IRequestContext requestContext, GUID tripId, int userId,
                                                     String senderFullName, String messageFromUser, String emailMode,
                                                     OptionalNotificationEmailData optionalNotificationEmailData, Boolean useCache, HttpServletRequest request) {
        JsonEmailCallbackResponse json;

        String errorString = String.format(
                "Scenario=TripItineraryEmail, UserId=%s, TripId=%s emailMode=%s", userId,
                tripId, emailMode);
        try {

            json = createEmailJSON(requestContext, tripId, userId, senderFullName, messageFromUser,
                    emailMode, optionalNotificationEmailData,useCache, request);
            map.put("EmailMode", emailMode);
            if(requestContext.getSite()!= null && !StringUtils.isEmpty(String.valueOf(requestContext.getSite().getId()))){
                map.put("EmailSiteId", String.valueOf(requestContext.getSite().getId()));
            }
            if(json != null && "SUCCESS".equalsIgnoreCase(json.getStatus())) {
                Properties properties = new Properties();
                properties.put("EmailSubject", String.valueOf(json.getEmailSubject()));
                properties.put("ItineraryNumber", String.valueOf(json.getItineraryNumber()));
                map.put("status", "EmailSuccess");
                itinCWLogger.log(map, requestContext);
                itineraryLogger.logSuccess(ItineraryEmailCallback.class.getName(), getPurchaseConfSuccessStatsdMessage(emailMode, json, requestContext), request, requestContext, properties, ItineraryLogger.EMAIL_CALLBACK_SUCCESS, "Success");
            }
        } catch (PurchaseConfirmEmailNotSentWhenTripIsNotBookedException e) {
            LOGGER.error(
                    SystemEvent.ITIN_PURCHASE_CONFIRM_EMAIL_NOT_SENT_WHEN_TRIP_IS_NOT_BOOKED,
                    errorString, e);
            String errorMessage = String
                    .format("%s, %s",
                            SystemEvent.ITIN_PURCHASE_CONFIRM_EMAIL_NOT_SENT_WHEN_TRIP_IS_NOT_BOOKED
                                    .getDescription(), e.getMessage());
            map.put("status", "EmailFailure");
            map.put("EmailErrorCode", SystemEvent.ITIN_PURCHASE_CONFIRM_EMAIL_NOT_SENT_WHEN_TRIP_IS_NOT_BOOKED.toString());
            itinCWLogger.log(map, requestContext);
            json = new JsonEmailCallbackResponse(errorMessage, ERROR_STATUS);

            itineraryLogger.increaseFailureCounter(ItineraryEmailCallback.class.getName(), getPurchaseConfNotSentWhenUnbookedStatsdMessage(emailMode, json, SystemEvent.ITIN_PURCHASE_CONFIRM_EMAIL_NOT_SENT_WHEN_TRIP_IS_NOT_BOOKED, requestContext), requestContext);
        } catch(TripUserValidationException tripValidationException){
            LOGGER.error(SystemEvent.ITINERARY_CONTEXT_MISMATCH_ERROR,errorString,tripValidationException);
            String errorMessage = String.format("%s, %s", SystemEvent.ITINERARY_CONTEXT_MISMATCH_ERROR.getDescription(), tripValidationException.getMessage());
            json = new JsonEmailCallbackResponse(errorMessage, ERROR_STATUS);
            map.put("status", "EmailFailure");
            map.put("EmailErrorCode", SystemEvent.ITINERARY_CONTEXT_MISMATCH_ERROR.toString());
            itinCWLogger.log(map, requestContext);
            itineraryLogger.increaseFailureCounter(ItineraryEmailCallback.class.getName(), getEmailFailedDueToTripAndContentUserMismatchStatsdMessage(emailMode, json, SystemEvent.ITINERARY_CONTEXT_MISMATCH_ERROR, requestContext), requestContext);
        } catch (Exception e) {
            LOGGER.error(SystemEvent.ITINERARY_DETAILS_GENERIC_ERROR,
                    errorString, e);
            String errorMessage = String.format("%s, %s",
                    SystemEvent.ITINERARY_DETAILS_GENERIC_ERROR
                            .getDescription(), e.getMessage());
            json = new JsonEmailCallbackResponse(errorMessage, ERROR_STATUS);

            itineraryLogger.increaseFailureCounter(ItineraryEmailCallback.class.getName(), getEmailGenericErrorStatsdMessage(emailMode, json, SystemEvent.ITINERARY_DETAILS_GENERIC_ERROR, requestContext), requestContext);
            map.put("status", "EmailFailure");
            map.put("EmailErrorCode", SystemEvent.ITINERARY_DETAILS_GENERIC_ERROR.toString());
            itinCWLogger.log(map, requestContext);
        }
        return json;
    }


    private String[] getNonResponsiveEmailSuccessStatsdMessage(String emailMode, JsonEmailCallbackResponse json, SimplifiedTrip simplifiedTrip, IRequestContext requestContext) {

        String[] statsdIndexArray = new String[]{"lob", itineraryLogger.getLoggerLob(simplifiedTrip) ,"bookingstatus", itineraryLogger.getBookingStatus(simplifiedTrip), "siteid",String.valueOf(requestContext.getSite().getId()), "productcode", json.getProductCode(), "emailmode", emailMode, "page", itineraryLogger.getPageBeanId(requestContext), ItinMetricsEnum.EMAIL_NONRESPONSIVE_CALLBACK_SUCCESS.value()};
        return statsdIndexArray;
    }

    private String[] getPurchaseConfSuccessStatsdMessage(String emailMode, JsonEmailCallbackResponse json, IRequestContext requestContext) {

        String[] statsdIndexArray = new String[]{"siteid",String.valueOf(requestContext.getSite().getId()),"productcode", json.getProductCode(), "emailmode", emailMode, "page", itineraryLogger.getPageBeanId(requestContext), ItinMetricsEnum.EMAIL_CALLBACK_SUCCESS.value()};
        return statsdIndexArray;
    }

    private String[] getPurchaseConfNotSentWhenUnbookedStatsdMessage(String emailMode, JsonEmailCallbackResponse json, SystemEvent systemEvent, IRequestContext requestContext) {
        String[] statsdIndexArray = new String[]{"emailmode", emailMode, "eventcode", String.valueOf(systemEvent.getEventId()), "page", itineraryLogger.getPageBeanId(requestContext), ItinMetricsEnum.EMAIL_NOT_SENT_WHEN_TRIP_IS_NOT_BOOKED.value()};
        return statsdIndexArray;
    }

    private String[] getEmailFailedDueToTripAndContentUserMismatchStatsdMessage(String emailMode, JsonEmailCallbackResponse json, SystemEvent systemEvent, IRequestContext requestContext) {
        String[] statsdIndexArray = new String[]{"emailmode", emailMode, "eventcode", String.valueOf(systemEvent.getEventId()), "page", itineraryLogger.getPageBeanId(requestContext), ItinMetricsEnum.EMAIL_TRIP_AND_CONTEXT_USER_MISMATCH.value()};
        return statsdIndexArray;
    }

    private String[] getEmailGenericErrorStatsdMessage(String emailMode, JsonEmailCallbackResponse json, SystemEvent systemEvent, IRequestContext requestContext) {
        String[] statsdIndexArray = new String[]{"emailmode", emailMode, "eventcode", String.valueOf(systemEvent.getEventId()), "page", itineraryLogger.getPageBeanId(requestContext), ItinMetricsEnum.EMAIL_GENERIC_ERROR.value()};
        return statsdIndexArray;
    }

    @SuppressWarnings({"PMD.NPathComplexity","PMD.CyclomaticComplexity","PMD.ExcessiveMethodLength"})
    private JsonEmailCallbackResponse createEmailJSON(IRequestContext requestContext, GUID tripId, int userId,
[O                                                      String senderFullName, String messageFromUser, String emailMode,
                                                      OptionalNotificationEmailData optionalNotificationEmailData, Boolean useCache, HttpServletRequest request)
            throws EmailHtmlGenerationException,
            PurchaseConfirmEmailNotSentWhenTripIsNotBookedException,
            TripUserValidationException{
        JsonEmailCallbackResponse json;

        if (!isValidEmailMode(emailMode)){
            throw new IllegalArgumentException(String.format("%s is not a valid emailMode", emailMode));
        }
        IStripingContext ctx = requestContext.getDefaultStripingContext();

        boolean shouldUseCachedTrip = toBooleanDefaultIfNull(useCache, false);

        ITrip trip = tripRepository.readTripHelper(requestContext, tripId, shouldUseCachedTrip);
        ItineraryCorePageModel model = pageModelBuilder.build(requestContext, trip);
        SimplifiedTrip simplifiedTrip;
        simplifiedTrip = simplifiedTripService.getTrip(new TripIDType(tripId.toString()), requestContext, shouldUseCachedTrip);
        showItinDetailsAction.reverseHotelGuestMultiPartName(simplifiedTrip, requestContext);
        String baseServerUrl = getBaseLinkURL(requestContext);
        String marketingAttributeCodes = null;

        PartnerLoyaltyConfirmationMsgModel partnerLoyaltyModel = partnerLoyaltyConfMsgBuilder.buildFromLoyalty(requestContext, trip, true);
        if(simplifiedTrip != null) {
            map.put("lob", simplifiedTrip.lob());
            if(partnerPointsConfig.isServiceEnabled(requestContext.getDefaultStripingContext())){
                model.setSimplifiedTrip(simplifiedTrip);
            }
        }
        ModelAndView mav = showItinDetailsAction.getView(request, model, requestContext);
        mav.addObject("showCustomMarketingBannerSection", itineraryDisplayConfiguration.isShowCustomMarketingBannerSection(new ResponsiveLOBStripingContext(emailMode, simplifiedTrip.lob(), simplifiedTrip.getBookingStatus(), requestContext.getDefaultStripingContext())));
        mav.addObject("isStatementCredit", LoyaltyBrandUtil.isStatementCredit(simplifiedTrip.getStatementCreditDetails()));
        mav.addObject("loyaltyBrand", LoyaltyBrandUtil.getloyaltyBrand(simplifiedTrip, requestContext, pwpConfig));
        mav.addObject("loyaltyBrandURL", LoyaltyBrandUtil.getloyaltyBrandURL(simplifiedTrip, requestContext, pwpConfig));
        mav.addObject("loyaltyBrandMsg", LoyaltyBrandUtil.populateLoyaltyBrandMsg(simplifiedTrip, requestContext, pwpConfig));
        mav.addObject("cssStyleRequired", itineraryDisplayConfiguration.isCssStyleRequired(requestContext.getDefaultStripingContext()));

        if(PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)
                && couponConfig.isCouponEnabled(new LobStripingContext(simplifiedTrip.getProductType(), requestContext.getDefaultStripingContext()))
                && isAmountGreaterThanThreshold(simplifiedTrip, requestContext.getDefaultStripingContext())){
            String couponCode = fetchCoupon(requestContext, trip);
            if(StringUtils.hasText(couponCode)){
                mav.addObject("couponCode", couponCode);
                mav.getModel().put("showCustomMarketingBannerSection", true);
                mav.addObject("customLogoUrl", itineraryDisplayConfiguration.getCustomLogoUrl(requestContext.getDefaultStripingContext()));
                mav.addObject("customMarketingButtonLink", itineraryDisplayConfiguration.getCustomMarketingButtonLink(requestContext.getDefaultStripingContext()));
            }else {
                LOGGER.info(UiSystemEvent.COUPON_GENERATION_FAILURE);
            }
        }

        if (itineraryDisplayConfiguration.isShowCustomMarketingBannerSection(new ResponsiveLOBStripingContext(emailMode, simplifiedTrip.lob(), simplifiedTrip.getBookingStatus(), requestContext.getDefaultStripingContext()))) {
            mav.addObject("customLogoUrl", itineraryDisplayConfiguration.getCustomLogoUrl(requestContext.getDefaultStripingContext()));
            mav.addObject("customMarketingButtonLink", itineraryDisplayConfiguration.getCustomMarketingButtonLink(requestContext.getDefaultStripingContext()));
        }
        mav.addObject("showClaimPointsSection", itineraryDisplayConfiguration.isShowClaimPointsSection(requestContext.getDefaultStripingContext()));
        mav.addObject("websiteServiceLink", getCustomerSupportUrl(requestContext, simplifiedTrip));

        addElevy(mav, trip, requestContext);

        boolean stopResponsive = isStopResponsive(request);
        if (!stopResponsive) {
            if (isResponsiveEnabled(simplifiedTrip, request.getParameter("forceResponsive"),  emailMode, requestContext)) {
                if (simplifiedTrip.isStandaloneFlight() || simplifiedTrip.isSplitTicketFlight()) {
                    return tripDetailEmailCallBackContorller.tripDetailsEmail(simplifiedTrip, "", emailMode, senderFullName, messageFromUser, requestContext, request, model, null); // need to pass the request & model for flights
                }

                String epcUrl = messageHotelEmailUrlBuilder.buildUrlForEPC(requestContext, trip);
                return tripDetailEmailCallBackContorller.tripDetailsEmail(simplifiedTrip, epcUrl, emailMode, senderFullName, messageFromUser, requestContext, trip);
            }
        }

        model.setIsEticket(itineraryDetailsHelper.isLegitimateEticket(simplifiedTrip));

        if(callbackControllerHelper.isStandaloneHotel(trip) && itineraryEmailPrintPageConfig.getIsUsingMobileFriendlyEmailView(new LobStripingContext(trip, ctx)))
        {
            mav.setViewName(MOBILEFRIENDLY_VIEW_NAME);
        }
        else
        {
            mav.setViewName(VIEW_NAME);
        }

        boolean evolable = false;
        if (model.getHasFlight()) {
            evolable = isEvolableWithFlights(model.getFlightModels(), requestContext);
        } else if (model.getHasPackage()){
            evolable = isEvolableWithPackages(model.getPackageModels(), requestContext);
        }
        mav.addObject("isEvolable", evolable);
        int productId = productTypeIDMapper.mapFrom(trip);
        mav.addObject("util", tripPageUtilities.getMapForFTLModel(requestContext));
        mav.getModel().put("senderFullName", secureRequestWrapperUtil.stripXSS(senderFullName));

        if(itineraryMessageTemplate.isShowTermsAndConditions(requestContext.getDefaultStripingContext())) {
            mav.getModel().put("privacyUrl",
                            urlUtils.buildUrlWithMarketingCodes(requestContext, baseServerUrl,
                                    itineraryMessageTemplate.getPrivacyPolicyUrlPattern(requestContext.getDefaultStripingContext()),
                                    emailMode, CHANGE_EMAIL_EVAR));
            mav.getModel().put("conditionsUrl", urlUtils.buildUrlWithMarketingCodes(requestContext, baseServerUrl,
                    itineraryMessageTemplate.getTermsAndConditionsUrlPattern(requestContext.getDefaultStripingContext()),
                    emailMode, CHANGE_EMAIL_EVAR));
        }
        if (itineraryMessageTemplate.isAddMarketingCodesToUrl(ctx)) {
            marketingAttributeCodes = urlUtils.getMarketingAttributeCodes(requestContext, emailMode, CHANGE_EMAIL_EVAR);

            mav.getModel().put("marketingAttributeCodes", marketingAttributeCodes);
        }

        mav.getModel().put("watMacro", itineraryMessageTemplate.getWatMacro(ctx));
        if (itineraryMessageTemplate.isShowRewardsActivitySection(ctx)) {
            String rewardsActivityLinkBase = baseServerUrl + itineraryMessageTemplate.getRewardsActivityLinkPath(ctx);
            String rewardsActivityLink = org.apache.commons.lang3.StringUtils.isNotEmpty(marketingAttributeCodes) ? urlUtils.addToUrl(rewardsActivityLinkBase, marketingAttributeCodes) : rewardsActivityLinkBase;

            addRewardsLoyaltyImageToModel(mav, simplifiedTrip, ctx);
            mav.getModel().put("rewardsActivityLink", rewardsActivityLink);
        }

        mav.getModel().put("messageFromUser", secureRequestWrapperUtil.stripXSS(messageFromUser));

        mav.getModel().put("emailIdentifier",
                getEmailIdentifier(requestContext, getEmailName(emailMode), productId));
        // To do: to be used in a ftl for itin change in the future
        mav.getModel().put("emailMode", emailMode);
        mav.getModel().put("serverName", baseServerUrl);
        mav.getModel().put("showATOLMessage",
                itineraryMessageTemplate.isShowATOLMessage(ctx));
        mav.getModel().put("showJVAddressInfo",
                itineraryMessageTemplate.isShowJVAddressInfo(ctx));
        mav.getModel().put("simplifiedTrip", simplifiedTrip);
        mav.getModel().put("partnerLoyaltyModel", partnerLoyaltyModel);
        if (partnerPointsConfig.isServiceEnabled(requestContext.getDefaultStripingContext())) {
            if (simplifiedTrip.getFlights() != null) {
                for (Flight flight : simplifiedTrip.getFlights()) {
                    mav.getModel().put(LoyaltyConstants.LOYALTY_PARTNER_DATA + "_AIR", flight.getpartnerLoyaltyDataMap());
                }
            }
            if (simplifiedTrip.isStandaloneActivities() || simplifiedTrip.isStandaloneMultipleLXActivities()) {
                mav.getModel().put("showCashAndPointsForPartner", true);
            }
        }
        mav.getModel().put("requestContext", requestContext);
        mav.getModel().put("migratedFlightUIEnabled", true);
        mav.getModel().put("migratedHotelUIEnabled", true);
        mav.getModel().put("containsFlightWithIncompleteTicketing", FlightTicketingUtil.containsFlightWithIncompleteTicketing(simplifiedTrip));
        mav.addObject("changeBookingOptionalData", optionalNotificationEmailData);
        if(optionalNotificationEmailData != null) {
            if(optionalNotificationEmailData.getEmailAddress() != null) {
                String emailAddress = optionalNotificationEmailData.getEmailAddress().trim();
                mav.getModel().put("emailAddress", emailAddress);
            }

            if(optionalNotificationEmailData.getLoyaltyPointsRefund() != null && !optionalNotificationEmailData.getLoyaltyPointsRefund().isEmpty()) {
                String loyaltyPointsRefund = optionalNotificationEmailData.getLoyaltyPointsRefund().trim();
                mav.getModel().put("loyaltyPointsRefund", loyaltyPointsRefund);
            }

            if(optionalNotificationEmailData.getLoyaltyCashRefund() != null && !optionalNotificationEmailData.getLoyaltyCashRefund().isEmpty()) {
                String loyaltyCashRefund = optionalNotificationEmailData.getLoyaltyCashRefund().trim();
                mav.getModel().put("loyaltyCashRefund", loyaltyCashRefund);
            }
        }

        //Set identify user from email link tag info
        setIdentifyUserByEmailLinkInfo(requestContext, mav, userId,ctx);
        boolean dctkEnabled = PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode) && featureConfig.isEmailDCTKEnabled(ctx);

        mav.getModel().put("dctkEnabled", dctkEnabled);

        mav.getModel().put("lccTFN","");
        mav.addObject("itinDetailsDateFormat", formatsApiHelper.getLongDatePattern(requestContext));
        String sessionGuid = requestContext.getClientInformation().getDeviceIdentifier().getId();
        mav.getModel().put("sessionGuid", sessionGuid);
        LobStripingContext lobStripingContext = new LobStripingContext(trip, requestContext.getDefaultStripingContext());
        mav.getModel().put("earnRewardsForGPSEnabled", itineraryEmailPrintPageConfig.isEarnRewardsForGPSEnabled(lobStripingContext));
        mav.getModel().put("countryCode", requestContext.getLocale().getCountry());
        if( itineraryMessageTemplate.isShowEmailSecurityInformation(requestContext.getDefaultStripingContext())) {
            mav.getModel().put("baseServerUrl", emailLinkUrl != null ? emailLinkUrl.getProtocolBaseUrl(requestContext.getSite().getTPID()) : "");
        }
        mav.getModel().put("showGPSCustomization", itineraryMessageTemplate.isShowGPSCustomization(requestContext.getDefaultStripingContext()));
        mav.getModel().put("showInsuranceSection", itineraryMessageTemplate.isShowInsuranceSection(requestContext.getDefaultStripingContext()));
        mav.getModel().put("lastName", InsuranceUIControlUtil.fetchLastName(requestContext));
        mav.getModel().put("brandName", InsuranceUIControlUtil.fetchBrandName(requestContext));
        mav.getModel().put("showCustomizedInsuranceMsg", itineraryDetailsInsuranceConfig.isShowCustomizedInsuranceMessage(requestContext.getDefaultStripingContext()));
        mav.getModel().put("switchEmailLogoEnabled",itineraryMessageTemplate.isSwitchEmailLogoEnabled(new LobStripingContext(simplifiedTrip.getProductType(),requestContext.getDefaultStripingContext())));

        Component lobComponent = null;
        if(!CollectionUtils.isEmpty(simplifiedTrip.getCars())) {
            lobComponent = simplifiedTrip.getCars().get(0);
        }else if(!CollectionUtils.isEmpty(simplifiedTrip.getHotels())) {
            lobComponent = simplifiedTrip.getHotels().get(0);
        }
        try{
            InsurancePostPurchaseCrossSell insurancePPCrossSell = insurancePostPurchaseXSellFactory.buildItinInsurancePostPurchaseCrossSellModel(lobComponent, simplifiedTrip, requestContext);
            InsurancePostPurchaseCrossSell legacyPackageInsurancePPCrossSell = insurancePostPurchaseXSellFactory.
                    buildPackageItinInsurancePostPurchaseCrossSellModel(simplifiedTrip, requestContext);
            if (legacyPackageInsurancePPCrossSell.isInsuranceXsellShow()) {
                mav.getModel().put("insurancePPCrossSell", legacyPackageInsurancePPCrossSell);
            } else {
                mav.getModel().put("insurancePPCrossSell", insurancePPCrossSell);
            }
        } catch (Exception e) {
            LOGGER.error("error while adding insurance cross sell in email for tripid : "+ simplifiedTrip.getTripId());
        }
        if(isPilotAlternateAbacusEmailEnabled(requestContext))
        {
            LOGGER.info("ItineraryEmailCallback-sessionGuid long: " + requestContext.getClientInformation().getDeviceIdentifier().toString());
        }
        boolean isStandaloneLCC = false;
        if(simplifiedTrip != null && simplifiedTrip.getFlights() != null && simplifiedTrip.getFlights().size() == 1)
            isStandaloneLCC = simplifiedTrip.getFlights().get(0).isLccOrCharter();

        if(isStandaloneLCC)
        {
            if(simplifiedTrip != null)
            {
                addLccTFN(mav,trip,requestContext,simplifiedTrip.getTripId());
            }
            else
            {
                addLccTFN(mav,trip,requestContext,"");
            }
        }

        if (dctkEnabled)
        {
            DCTKData dctk = new DCTKData(requestContext, DCTKData.EMAIL_TYPE_PURCHASECONFIRMATION, tripId, model.getCustomerFacingTripIdentifier());
            mav.getModel().put("dctkData", secureRequestWrapperUtil.stripXSS(dctk.toString()));
        }

        //put void expiration time to trip details email ftl
        mav.getModel().put("voidExpirationTime", this.getVoidExpiryString(simplifiedTrip, requestContext, emailMode, trip));

        if (isAirChangeServiceToBeCalled(simplifiedTrip, requestContext)&& PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)) {
            List<IAirPenaltyInfo> airPenaltyInfos = this.getAirPenaltyInfos(simplifiedTrip.getFlights(),requestContext, trip);
            this.addAirChangePenaltyInfo(mav.getModel(), simplifiedTrip.getFlights(),airPenaltyInfos, requestContext);
        }
        if(featureConfig.isDisplayLXDisneyModuleEnabled(requestContext.getDefaultStripingContext()) &&  simplifiedTrip != null && simplifiedTrip.getFlights() != null)
        {
            addLXDisneyModel(mav,requestContext,simplifiedTrip);
        }

        if(itineraryMessageTemplate.isShowCreditCardLastFourDigits(requestContext.getDefaultStripingContext())) {
            mav.getModel().put("customerLastFourNumbersOfCreditCard", bankLoyaltyCreditCardService.getLastFourDigitsCreditCardNumber(simplifiedTrip.getTripId(), requestContext));
        }

        String emailBody = getHtmlFromModelAndView(mav,
                requestContext.getLocale());

        long langId = requestContext.getLangId();
        String languageCode = langIdToLocaleTranslator
                .getTranslatedLocaleOrNull(String.valueOf(langId),  requestContext.getDefaultStripingContext());

        if (PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)
                && isUnbooked(model)) {
            // ENS MATCHES THE MESSAGE FOR ERROR-DO NOT CHANGE
            throw new PurchaseConfirmEmailNotSentWhenTripIsNotBookedException(
                    "PURCHASE CONFIRMATION EMAIL NOT SENT BECAUSE TRIP IS NOT BOOKED");
        }

        String emailSubject = getSubject(model, emailMode, simplifiedTrip, requestContext, siteBrandModelBuilder.build(requestContext));


        String trl = "";
        if(trip.getItineraryKey()!=null){
            trl = Long.toString(trip.getItineraryKey().getTravelRecordLocator());
        }
        json = new JsonEmailCallbackResponse(emailBody, emailSubject,
                SUCCESS_STATUS, String.valueOf(productId), languageCode, trl);

        if (ITIN_CHANGE.equalsIgnoreCase(emailMode)
                || PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)) {
            json.setEmailMode(emailMode);
        }

        if (PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)) {
            String customerFacingItinNumber =   String.valueOf(trip.getCustomerFacingTripIdentifier());
            addCustomerFacingItinNumber(customerFacingItinNumber,json);
            addTicketingInfo(model, json);
        }

        itineraryLogger.logSuccess(ItineraryEmailCallback.class.getName(), getNonResponsiveEmailSuccessStatsdMessage(emailMode, json, simplifiedTrip, requestContext), request, requestContext, itineraryLogger.getTripDetailsSuccessProperties(simplifiedTrip.getTripId(),simplifiedTrip), ItineraryLogger.EMAIL_NONRESPONSIVE_CALLBACK_SUCCESS, "Success");
        return json;
    }

    private void addRewardsLoyaltyImageToModel(ModelAndView mav, SimplifiedTrip trip, IStripingContext stripingContext) {
        CustomerCareCustomerType tier = trip.getCustomerCareCustomerType();
        if (tier == null) {
            return;
        }

        final String tierAsString = tier.toString();
        if (itineraryMessageTemplate.getCustomerTypeEligibleForRewards(stripingContext).contains(tierAsString)) {
            mav.getModel().put(
                    "rewardsLoyaltyImagePath",
                    String.format(
                            itineraryMessageTemplate.getRewardsLoyaltyLevelImagePath(stripingContext),
                            tierAsString.toLowerCase(Locale.ENGLISH)
                    )
            );
        }
    }

    private String getCustomerSupportUrl(IRequestContext requestContext, SimplifiedTrip simplifiedTrip) {
        if(simplifiedTrip.getCustomerSupport() != null && !requestContext.getSite().isHCom()) {
            return simplifiedTrip.getCustomerSupport().getCustomerSupportURL();
        }
        return customerCareConfig.getItineraryCustomerSupportURL(requestContext.getDefaultStripingContext());
    }

    private void addLXDisneyModel(ModelAndView mav, IRequestContext requestContext, SimplifiedTrip simplifiedTrip) {
            List<Flight> flights = simplifiedTrip.getFlights();
            if (CollectionUtils.isNotEmpty(flights)) {
                Flight flight = flights.get(0);
                String lxDisneyCaliforniaAirportCodes = featureConfig.getCaliforniaDisneySupportedAirportCodes(requestContext.getDefaultStripingContext());
                String lxOrlandoDisneySupportedAirportCodes = featureConfig.getOrlandoDisneySupportedAirportCodes(requestContext.getDefaultStripingContext());
                String arrivalAirportCode = flight.getLegs().get(flight.getLegs().size() - 1).getArrivalAirport().getAirportCode();
                try {

                    if (lxDisneyCaliforniaAirportCodes.contains(arrivalAirportCode)) {
                        mav.getModel().put("californiaDisneySupportedAirportCode", true);
                    } else if (lxOrlandoDisneySupportedAirportCodes.contains(arrivalAirportCode)) {
                        mav.getModel().put("orlandoDisneySupportedAirportCode", true);
                    }


                } catch (Exception e) {
                    LOGGER.error(simplifiedTrip.getTripId() + "LX disney model builder exception " + e);
                }
            }
    }

    private boolean isAmountGreaterThanThreshold(SimplifiedTrip simplifiedTrip, IStripingContext stripingContext){
        return (simplifiedTrip != null) && (simplifiedTrip.getPaymentSummary() != null) && (simplifiedTrip.getPaymentSummary().getTotalPaidPrice() != null)
                && (Double.compare(simplifiedTrip.getPaymentSummary().getTotalPaidPrice().getDouble(), Double.parseDouble(couponConfig.getCouponThresholdPrice(stripingContext))) >= 0);
    }

    private String fetchCoupon(IRequestContext requestContext, ITrip trip) {
        return couponService.generateCoupon(requestContext, trip);
	}

    private void addElevy(ModelAndView mav, ITrip trip, IRequestContext requestContext) {
        ElevyModel elevyModel = elevyModelBuilder.build(trip, requestContext);
        mav.getModel().put(ELEVY, elevyModel);
    }

    private boolean isEvolableWithPackages(List<PackageModel> packageModels, IRequestContext requestContext)
    {

        if (CollectionUtils.isNotEmpty(packageModels)) {
            for (PackageModel packageModel : packageModels) {
                return isEvolable(packageModel.getFlightModel(), requestContext.getDefaultStripingContext());
            }
        }
        return false;
    }

    private boolean isEvolableWithFlights(List<FlightModel> flightModels, IRequestContext requestContext)
    {
        if (CollectionUtils.isNotEmpty(flightModels)) {
            for (FlightModel flightModel : flightModels) {
                return isEvolable(flightModel, requestContext.getDefaultStripingContext());
            }
        }
        return false;
    }

    private boolean isEvolable(FlightModel flightModel, IStripingContext stripingContext)
    {
        if (flightModel != null) {
            IFlightProduct flightProduct = flightModel.getFlightProduct();
            return evolableHelper.isEvolableWithFlightProduct(flightProduct, stripingContext);
        }
        return false;
    }

    private boolean isStandAloneRail(ITrip domainTrip){
        return domainTrip.getFirstComponent(ProductType.Rail) != null ? true : false;
    }

    private boolean isStopResponsive(HttpServletRequest request) {
        return (request.getParameter("stopResponsive") != null &&
                Boolean.parseBoolean(request.getParameter("stopResponsive")));
    }

    private boolean isPilotAlternateAbacusEmailEnabled(IRequestContext requestContext){
        return featureConfig.isPilotAlternateAbacusEmailEnabled(requestContext.getDefaultStripingContext());
    }

    private List<IAirPenaltyInfo> getAirPenaltyInfos(List<Flight>flights, IRequestContext requestContext, ITrip trip) {
        List<IAirPenaltyInfo> airPenaltyInfos = new ArrayList<IAirPenaltyInfo>();

        try {
            airPenaltyInfos = airChangeService.getCancelPenaltyInfo(getAirRecordLocators(flights), AbacusInfo.emptyAbacusInfo(), requestContext, trip);
            return airPenaltyInfos;
        } catch (Exception e) {
            LOGGER.error(SystemEvent.AIR_CHANGE_SERVICE_GENERIC_ERROR,
                    String.format("Error occurred while trying to get air penalty info for ARL(s): "
                            + com.expedia.www.shared.ui.utils.StringUtils.convertListsToCommaDelimitedString(getAirRecordLocators(flights)), e));
            return airPenaltyInfos;
        }
    }

    private void addLccTFN(ModelAndView mav,ITrip trip,IRequestContext requestContext,String itinNumber)
    {
        String airlineCode = getArilineCodeFromTrip(trip);
        if(airlineCode != null && !"".equals(airlineCode)) {
            try{
                IAirCarrierDetails airCarrierDetails = airCarrierDetailsService.getAirCarrierDetails(airlineCode, "RESERVATIONCONTACT", requestContext);
                if(airCarrierDetails != null && airCarrierDetails.getAirCarrierDetails() != null && airCarrierDetails.getAirCarrierDetails().size() > 0)
                    mav.getModel().put("lccTFN", airCarrierDetails.getAirCarrierDetails().get(0).getCarrierDetailsString());
            }catch(Exception ex){
                LOGGER.error(UiSystemEvent.AIR_CARRIER_DETAILS_GENERIC_ERROR, "error occurred while trying to get airlinecode for email_callback_body page. AirlinwCode:" + airlineCode + ",itinerarynumber:" + itinNumber, ex);
            }
        }
    }

    private boolean isAirChangeServiceToBeCalled(SimplifiedTrip simplifiedTrip, IRequestContext requestContext) {
        return simplifiedTrip != null && !CollectionUtils.isEmpty(simplifiedTrip.getFlights()) &&
                featureConfig.isACSCallOnPurchaseConfirmationEmailLoadEnabled(requestContext.getDefaultStripingContext());
    }

    private String getArilineCodeFromTrip(ITrip trip)
    {
        IFlightComponent flightComponent = trip.getFirstFlightComponent();
        if(flightComponent != null && flightComponent.hasBookedItems())
        {
            IFlightProduct flightProduct  = trip.getFirstFlightProduct();
            if(flightProduct != null)
            {
                IFlightTrip flightTrip = flightProduct.getFlightTrip();
                List<IFlightLeg> legs = flightTrip.getLegs();
                if(legs != null || legs.size() > 0)
                {
                    IFlightSegment firstSegment = legs.get(0).getSegments().get(0);
                    return firstSegment.getAirline().getAirlineCode();
                }
            }
        }
        return "";
    }

    private List<String> getAirRecordLocators(List<Flight> flights) {
        List<String> airRecordLocators = new ArrayList<String>();

        for (Flight flight : flights) {
            String airRecordLocator = flight.getAirRecordLocator();
            if (StringUtils.hasText(airRecordLocator)) {
                airRecordLocators.add(airRecordLocator);
            } else {
                LOGGER.warn(String.format("Error occurred before try to get air penalty info for ARL(s): the ARL of flight %s has not text",
                        flight.getUniqueID()));
                return airRecordLocators;
            }
        }

        if(airRecordLocators.size() == 0){
            LOGGER.warn(String.format("Error occurred before try to get air penalty info for ARL(s): there is no ARLs"));
            return airRecordLocators;
        }
        return airRecordLocators;
    }
    /**
     * Add AirChangePenaltyInfo for call propensity T&L implementation
     * @param model
     * @param flights
     * @param airPenaltyInfos
     * @param requestContext
     */
    private void addAirChangePenaltyInfo(Map<String, Object> model, List<Flight> flights, List<IAirPenaltyInfo> airPenaltyInfos, IRequestContext requestContext) {
        List<String> airRecordLocators = getAirRecordLocators(flights);
        if(airRecordLocators == null){
            return;
        }
        if (CollectionUtils.isEmpty(airPenaltyInfos) || airPenaltyInfos.get(0) == null) {
            return;
        }

        if (isCancelActionUnActionable(airPenaltyInfos.get(0).getCancelAction())&& isExchangeActionUnActionable(airPenaltyInfos.get(0).getExchangeAction())) {
            return;
        }


        if (!isCancelActionUnActionable(airPenaltyInfos.get(0).getCancelAction())) {
            putTotalCancelPenalty(model, airPenaltyInfos);
        }

        if (!isExchangeActionUnActionable(airPenaltyInfos.get(0).getExchangeAction())) {
            putTotalChangePenalty(model, airPenaltyInfos, requestContext.getLocale(), requestContext.getDefaultStripingContext());
        }

    }

    private void putTotalChangePenalty(Map<String, Object> model, List<IAirPenaltyInfo> airPenaltyInfos, Locale locale, IStripingContext ctx) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        if(airPenaltyInfos.get(0).getTotalExchangePenalty() == null){
            LOGGER.info(SystemEvent.AIR_CHANGE_SERVICE_PENALTYINFO_NOT_AVAILABLE,
                    String.format("Total Change Penalty not found for ARL:%s and PNR:%s", airPenaltyInfos.get(0).getARL(), airPenaltyInfos.get(0).getPNR()),
                    null);
        }else{
            IPrice totalChangePenalty = null;
            for(IAirPenaltyInfo airPenaltyInfo: airPenaltyInfos){
                if(totalChangePenalty == null){
                    totalChangePenalty = airPenaltyInfo.getTotalExchangePenalty();
                } else {
                    if (airPenaltyInfo.getTotalExchangePenalty() != null) {
                        totalChangePenalty.add(airPenaltyInfo.getTotalExchangePenalty());
                    }
                }
            }

            LocalizedPrice localizedPrice = new LocalizedPrice(totalChangePenalty, locale, ctx);
            model.put("totalChangePenalty", localizedPrice.format());
        }
    }

    private void putTotalCancelPenalty(Map<String, Object> model, List<IAirPenaltyInfo> airPenaltyInfos) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        if(airPenaltyInfos.get(0).getTotalCancelPenalty() ==null){
            LOGGER.info(SystemEvent.AIR_CHANGE_SERVICE_PENALTYINFO_NOT_AVAILABLE,
                    String.format("Total Cancel Penalty not found for ARL:%s and PNR:%s", airPenaltyInfos.get(0).getARL(), airPenaltyInfos.get(0).getPNR()),
                    null);
        }else {
            IPrice totalCancelPenalty = null;
            for(IAirPenaltyInfo airPenaltyInfo : airPenaltyInfos){
                if(totalCancelPenalty == null){
                    totalCancelPenalty = airPenaltyInfo.getTotalCancelPenalty();
                }else{
                    if (airPenaltyInfo.getTotalCancelPenalty() != null) {
                        totalCancelPenalty.add(airPenaltyInfo.getTotalCancelPenalty());
                    }
                }
            }

            if (totalCancelPenalty.getAmount().intValue() == totalCancelPenalty.getAmount().floatValue()) {
                //if it is int then ignore the fraction
                model.put("totalCancelPenalty", String.valueOf(totalCancelPenalty.getAmount().intValue()));
            } else {
                model.put("totalCancelPenalty", df.format(totalCancelPenalty.getAmount()));
            }
        }
    }

    private String getVoidExpiryString(SimplifiedTrip simplifiedTrip, IRequestContext requestContext, String emailMode, ITrip trip ) {

        String voidExpiryString = null;

        if (!isAirChangeServiceToBeCalled(simplifiedTrip, requestContext)) {
            return voidExpiryString;
        }

        if (!PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)) {
            return voidExpiryString;
        }

        List<IAirPenaltyInfo> airPenaltyInfos = this.getAirPenaltyInfos(simplifiedTrip.getFlights(),requestContext, trip);

        if (null != airPenaltyInfos && airPenaltyInfos.size() > 0 && null != airPenaltyInfos.get(0).getVoidDateTime()) {
            try {
                voidExpiryString = airPenaltyInfos.get(0).getVoidDateTime().toString();

            } catch (Exception e) {
                LOGGER.error(SystemEvent.AIR_CHANGE_SERVICE_GENERIC_ERROR,
                        String.format("Error occurred while trying to get air penalty info void date time for void expiration: "
                                + com.expedia.www.shared.ui.utils.StringUtils.convertListsToCommaDelimitedString(getAirRecordLocators(simplifiedTrip.getFlights())), e));
                return voidExpiryString;
            }
        }
        return  voidExpiryString;
    }

    // if the ExchangeAction is any of the below ones, dont bother getting the PenaltyInfo
    private boolean isExchangeActionUnActionable(ExchangeActionEnum exchangeAction) {
        return (exchangeAction == ExchangeActionEnum.NO_ACTION || exchangeAction == ExchangeActionEnum.NOT_SET || exchangeAction == ExchangeActionEnum.UNKNOWN);
    }

    // if the CancelAction is any of the below ones, dont bother getting the PenaltyInfo
    private boolean isCancelActionUnActionable(CancelActionEnum cancelAction) {
        return (cancelAction == CancelActionEnum.NO_ACTION || cancelAction == CancelActionEnum.NOT_SET || cancelAction == CancelActionEnum.UNKNOWN || cancelAction == CancelActionEnum.UNTICKETED_CANCEL);
    }

    void addTicketingInfo(ItineraryCorePageModel model,
                          JsonEmailCallbackResponse json) {

        if (model.getHasFlight() || model.getHasPackage())

        {

            FlightModel flightModel = null;

            if (model.getHasFlight()) {
                flightModel = model.getFlightModels().get(0);
            } else if (model.getHasPackage()) {
                flightModel = model.getPackageModels().get(0).getFlightModel();
            }

            if (null != flightModel) {

                String confirmationCode = getConfirmationCode(flightModel);
                String ticketNumber = getTicketNumber(flightModel);

                json.setConfirmationCode(confirmationCode);
                json.setTicketNumber(ticketNumber);

            }

        }

    }
    private void addCustomerFacingItinNumber(String customerItinNumber,JsonEmailCallbackResponse json)
    {
        json.setItineraryNumber(customerItinNumber);
    }

    private String getConfirmationCode(FlightModel flightModel) {
        List<ConfirmationCode> confirmationCodes = flightModel
                .getConfirmationCodes();
        if (null != confirmationCodes && !confirmationCodes.isEmpty()) {
            return confirmationCodes.get(0).getConfirmationCode();
        }

        return null;
    }

    private String getTicketNumber(FlightModel flightModel) {
        List<FlightTravelerUIModel> travelers = flightModel
                .getFlightTravelerUIModelList();

        if (null != travelers) {
            for (FlightTravelerUIModel traveler : travelers) {
                // not infant in lap
                if (traveler.getAllowFrequentFlyerSelection()) {
                    List<String> ticketNumbers = traveler
                            .getAirlineTicketNumbers();

                    if (null != ticketNumbers && !ticketNumbers.isEmpty()) {
                        return ticketNumbers.get(0);
                    }
                }

            }

        }

        return null;
    }

    protected boolean isUnbooked(ItineraryCorePageModel model)
    {
        boolean isUnbooked = false;
        if (model.getHasPackage()) {
            isUnbooked = isUnbookedPackage(model);
        } else if (model.getHasFlight()) {
            isUnbooked = isUnbookedFlight(model);
        } else if (model.getHasHotel()) {
            isUnbooked = isUnbookedHotel(model);
        }
        return isUnbooked;
    }

    private boolean isUnbookedHotel(ItineraryCorePageModel model){
        for (HotelModel hotelModel : model.getHotelModels()) {
            if (hotelModel.isUnbooked()) {
                return true;
            }
        }
        return false;
    }

    private boolean isUnbookedFlight(ItineraryCorePageModel model){
        for (FlightModel flightModel : model.getFlightModels()) {
            if (flightModel.isUnbooked()) {
                return true;
            }
        }
        return false;
    }

    private boolean isUnbookedPackage(ItineraryCorePageModel model){
        for (PackageModel packageModel : model.getPackageModels()) {
            if (packageModel.isUnbooked()) {
                return true;
            }
        }
        return false;
    }

    protected String getBaseLinkURL(IRequestContext requestContext) {
        if(Environment.getEnvironment().compareTo(EnvironmentVersionType.INTEGRATION) == 0
                && itineraryMessageTemplate.isOverrideIntegrationHostNameEnabled(requestContext.getDefaultStripingContext())){
           return emailLinkBaseUrl.getOverriddenBaseUrl(requestContext,
                   itineraryMessageTemplate.getOverriddenIntegrationHostName(requestContext.getDefaultStripingContext()), true);
        }

        return emailLinkUrl.getProtocolBaseUrl(requestContext,
                itineraryMessageTemplate.isAlternateDomainNameEnabled(requestContext.getDefaultStripingContext()));
    }

    protected String getSubject(ItineraryCorePageModel model, String emailMode, SimplifiedTrip simplifiedTrip,
            IRequestContext requestContext, SiteBrandModel siteBrandModel) {

        final String subject = getSubjectInteral(model,emailMode,simplifiedTrip,requestContext, siteBrandModel);
        return RESOURCE_BUNDLE_ADAPTER_FACTORY.build(SimpleSiteContextBuilder.getContext(siteBrandModel)).applyRules(subject);
    }

    private String getSubjectInteral(ItineraryCorePageModel model, String emailMode, SimplifiedTrip simplifiedTrip,
                                IRequestContext requestContext, SiteBrandModel siteBrandModel) {

        IStripingContext ctx = requestContext.getDefaultStripingContext();
        String eTicketText = getETicketString(simplifiedTrip, ctx, requestContext.getLocale());
        DateRange tripDateRange = model.getTrip().getDateRange();
        LocalizedDateTime localizedStartDate = tripDateRange.getLocalizedStartDateTime();
        LocalizedDateTime localizedEndDate   = tripDateRange.getLocalizedEndDateTime();
        String itineraryTitle = model.getTripTitle();
        String itineraryNumber = model.getCustomerFacingTripIdentifier();

        if (PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)) {

            if (itineraryMessageTemplate.isGenerateEmailSubjectUsingFTLEnabled(requestContext.getDefaultStripingContext())) {
                ModelAndView modelAndView = new ModelAndView(TRIP_PURCHASE_CONF_EMAIL);
                modelAndView.addObject("brandName", siteBrandModel.getBrandname());
                modelAndView.addObject("customerFacingTripNumber", Long.toString(simplifiedTrip.getTripNumber()));
                modelAndView.addObject("localizedStartDate", localizedStartDate.getMediumDate());
                try {
                    return getHtmlFromModelAndView(modelAndView, requestContext.getLocale());
                } catch (EmailHtmlGenerationException e) {
                    LOGGER.error(UiSystemEvent.PURCHASE_EMAIL_HTML_GENERATION_ERROR, UiSystemEvent.PURCHASE_EMAIL_HTML_GENERATION_ERROR.getDescription(), e);
                }
            }

            String successfulIBPBookingEmailSubjectHeader = this.messageSource
                    .getMessage(
                            "email.subject.successfulBookingEmail",
                            new Object[] { namesForDisplayConfig.getBrandName(ctx) },
                            String.format("%s travel confirmation",
                                    namesForDisplayConfig.getBrandName(ctx)),
                            requestContext.getLocale());


            String itinNumber = this.messageSource.getMessage("email.subject.ItinNumber",new Object[]{itineraryNumber}, "Itin# " + itineraryNumber,requestContext.getLocale());

            return String.format("%s - %s - " + itinNumber,
                    successfulIBPBookingEmailSubjectHeader + eTicketText,
                    localizedStartDate.getMediumDate());
        } else if (ITIN_CHANGE.equalsIgnoreCase(emailMode)) {

            String itineraryChangeEmailSubjectHeader = this.messageSource
                    .getMessage("email.subject.itineraryChangeEmail", null,
                            "Updated Itinerary", requestContext.getLocale());
            String defaultItineraryEmailSubjectHeader = this.messageSource
                    .getMessage("email.subject.defaultItineraryEmail", null,
                            "Itinerary", requestContext
                                    .getLocale());
            return String.format("%s - %s, %s - %s (%s #%s)",
                    itineraryChangeEmailSubjectHeader, itineraryTitle + eTicketText,
                    localizedStartDate.getMediumDate(), localizedEndDate.getMediumDate(),
                    defaultItineraryEmailSubjectHeader, itineraryNumber);
        } else {

            String itineraryEmailSubjectHeader = this.messageSource.getMessage(
                    "email.subject.defaultItineraryEmail", null, "Itinerary",
                    requestContext.getLocale());

            if (localizedStartDate != null && localizedEndDate != null) {
                return String.format("%s - %s, %s - %s (%s #%s)",
                        itineraryEmailSubjectHeader, itineraryTitle + eTicketText,
                        localizedStartDate.getMediumDate(),
                        localizedEndDate.getMediumDate(),
                        itineraryEmailSubjectHeader, itineraryNumber);
            } else {
                return String.format("%s - %s (%s #%s)",
                        itineraryEmailSubjectHeader, itineraryTitle,
                        itineraryEmailSubjectHeader, itineraryNumber);
            }
        }
    }

    protected String getEmailIdentifier(IRequestContext requestContext, String emailName, int productId) {
        Site site = requestContext.getSite();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss ",
                requestContext.getLocale());
        String timestamp = dateFormat.format(Calendar.getInstance().getTime());

        return String.format(
                "(EMID: ETM_%s_%s.%s_X_X_X_EN_%s)(MD: %s)(ETID: 0)", emailName,
                site.getTPIDAsString(), site.getEapIdAsString(), productId, timestamp);
    }

    protected String getEmailName(String emailMode) {
        String emailName;

        if (PURCHASE_CONFIRMATION.equalsIgnoreCase(emailMode)) {
            emailName = "ENSPC";
        } else {
            emailName = "ENSIE";
        }

        return emailName;
    }

    public class PurchaseConfirmEmailNotSentWhenTripIsNotBookedException extends
            Exception {
        public PurchaseConfirmEmailNotSentWhenTripIsNotBookedException(
                String message) {
            super(message);
        }
    }

    public void setTripRepository(ITripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void setShowItinDetailsAction(
            ShowItinDetailsAction showItinDetailsAction) {
        this.showItinDetailsAction = showItinDetailsAction;
    }

    public void setPageModelBuilder(
            ItineraryCorePageModelBuilder pageModelBuilder) {
        this.pageModelBuilder = pageModelBuilder;
    }

    public void setProductTypeIDMapper(ProductTypeIdMapper productTypeIDMapper) {
        this.productTypeIDMapper = productTypeIDMapper;
    }

    public void setLangIdToLocaleTranslator(
            ILocaleLangIdTranslator langIdToLocaleTranslator) {
        this.langIdToLocaleTranslator = langIdToLocaleTranslator;
    }

    public void setSettings(IItineraryLocalizationSettings settings) {
        this.settings = settings;
    }

    public void setEmailLinkBaseUrl (EmailLinkBaseUrl emailLinkUrl)
    {
        this.emailLinkUrl= emailLinkUrl;
    }

    public void setCallbackControllerHelper(ItineraryDetailsHelper callbackControllerHelper) {
        this.callbackControllerHelper = callbackControllerHelper;
    }

    protected IRequestContext getContextWithSpecifiedUser(int userId, HttpServletRequest request)
    {
        return contextFactory.rebuildContextWithSpecifiedUserAndCacheThemBoth(request, getUser(userId, request));
    }

    protected IUser getUser(int userId, HttpServletRequest request)
    {
        IUser user = new User(userId);  //This builds a default dummy user with the TUID passed in, but it's a Guest User
        try {
            if (userId <= 0) {
                throw new IllegalArgumentException();
            }
            final IUser expUser = identityDomain.findUser(userId, contextFactory.getContext(request));  //This attempts to get the full user if it's not a guest user
            if (expUser != null && expUser.getLegacyId() > 0) {
                user = expUser;  //if things go well, we will use the found User
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("TUID passed to EmailCallback was invalid (zero or negative). Userid: " + userId);
        } catch (Exception e) {
            LOGGER.warn("Unable to find tuid " + userId + " from identityDomain.  Using default guest User.");
        }

        return user;
    }

    private void setIdentifyUserByEmailLinkInfo(IRequestContext requestContext, ModelAndView mav, int tuid, IStripingContext ctx){
        IUser user = null;
        Map<String, Object> emailIdentifiedUserModel = new HashMap<String, Object>();
        try {
            user = userAuthenticator.getIdentifiedUser(requestContext, tuid);
        } catch(Exception e){
            //Do nothing and ignore runtime exception.
            LOGGER.info("exception threw from user service. Can not set email link information for identifying user.", e);
        }
        if ( null != user && null != user.getEmail() && StringUtils.hasText(user.getEmail().getStr())){
            Name name = null;
            if (null != user.getName()) {
                name = new Name((null == user.getName().getFirstName())? "": user.getName().getFirstName(), "",
                        (null == user.getName().getLastName())? "": user.getName().getLastName());
            }  else {
                name = new Name("","","");
            }
            populateEncryptedUserDetails(requestContext,user,name,mav,ctx);
            emailIdentifiedUserModel.put("emailAddress", user.getEmail().getStr().trim());
            emailIdentifiedUserModel.put("firstName", name.getFirstName().trim());
            emailIdentifiedUserModel.put("lastName", name.getLastName().trim());
        } else {
            emailIdentifiedUserModel.put("emailAddress", "");
            emailIdentifiedUserModel.put("firstName", "");
            emailIdentifiedUserModel.put("lastName", "");
            LOGGER.info("Could not retrieve user info from database. Therefore could not identifiy the user in email link");
        }
        mav.getModel().putAll(emailIdentifiedUserModel);
        mav.getModel().put("emailIdentifiedUserModel", emailIdentifiedUserModel);
    }

    private void populateEncryptedUserDetails(IRequestContext requestContext, IUser user, Name name, ModelAndView mav, IStripingContext ctx) {
        if(!featureConfig.isEncryptEmailAddressInEmailEnabled(ctx)){
            mav.getModel().put("encryptEmailAddress",false);
            return;
        }
        mav.getModel().put("encryptEmailAddress",true);
        String emailAddress= user.getEmail().getStr().trim();
        String firstName = name.getFirstName().trim();
        String lastName = name.getLastName().trim();
        String expuid = user.isExpUser() ? String.valueOf(user.getExpUserId()) : null;
        userDetails = new UserDetails(emailAddress, null, expuid,
                firstName, lastName, System.currentTimeMillis());
        String userTokenString = "";
        if(itineraryDetailsValueConfiguration.isNewEmailClickHandlerEnabled(ctx)){
            userTokenString = userTokenGenerateService.getUserToken(requestContext, emailAddress,null,expuid,firstName,lastName,System.currentTimeMillis());
        }
        if(StringUtils.isEmpty(userTokenString)) {
            try {
                userToken = new UserToken();
                userTokenString = userToken.encrypt(userDetails,identityTokenconfig.getItinerarySharedSecret(ctx));
                mav.getModel().put("userTokenString",userTokenString);
            } catch (IdentityTokenException e) {
                LOGGER.error("unable to encrypt the userdetails");
                mav.getModel().put("encryptEmailAddress",false);
            }
        } else {
            mav.getModel().put("userTokenString",userTokenString);
        }

    }

    private String getETicketString(SimplifiedTrip simplifiedTrip, IStripingContext ctx, Locale locale) {
        if (callbackControllerHelper.isLegitimateEticket(simplifiedTrip) && itineraryMessageTemplate.isShowEticketMessaging(ctx))
        {
            return this.messageSource.getMessage("email.subject.e-ticket", null, "/e-Ticket", locale);
        }
        else
        {
            return "";
        }
    }

    private boolean isResponsiveEnabled(SimplifiedTrip simplifiedTrip, String forceResponsive, String emailMode, IRequestContext requestContext) {

        final String lob = simplifiedTrip.lob();
        return responsiveAbacusHelper.isPurchaseConfirmationResponsiveEmailEnabled(requestContext, simplifiedTrip, lob, emailMode) ||
                responsiveAbacusHelper.isItinSelfSentResponsiveEmailEnabled(requestContext, simplifiedTrip, lob, emailMode) ||
                responsiveAbacusHelper.isItinChangeResponsiveEmailEnabled(requestContext, simplifiedTrip, lob, emailMode) ||
                responsiveAbacusHelper.isItinUpgradeResponsiveEmailEnabled(requestContext, simplifiedTrip, lob, emailMode) ||
                (forceResponsive != null && forceResponsive.equals("true"));
    }

    private static boolean isLongGUID(final String guid) {
        return null != guid && LONG_GUID_PATTERN.matcher(guid).matches();
    }

    private boolean isValidEmailMode(String emailMode) {
        List<String> emailModes = Arrays.asList(new String[]{"",ITIN_CHANGE, ITIN_UPGRADE, PURCHASE_CONFIRMATION, ITIN});
        return emailModes.contains(emailMode);

    }
}

