import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

/* =========================================================
   1. DICTIONARY BANK (Giả lập AI Knowledge Base)
   Kho từ vựng để map với % Churn Risk (Rủi ro nghỉ việc)
========================================================= */

const KEYWORDS_HIGH_RISK = [ // Risk > 70%
  "Volatile", "Disengaged", "Burnt out", "Detached", "Restless", "Passive", 
  "Seeking", "Critical", "Unsatisfied", "Disconnecting", "Withdrawn", "Fluctuating",
  "Overworked", "Stagnant", "Demotivated", "Unchallenged", "Frustrated", "Looking",
  "External-focused", "Silent", "Non-responsive", "Declining", "Resistant"
];

const KEYWORDS_MED_RISK = [ // Risk 30% - 70%
  "Steady", "Routine", "Average", "Consistent", "Neutral", "Observant", 
  "Waiting", "Developing", "Dependent", "Ambivalent", "Content", "Stable",
  "Reserved", "Listening", "Adjusting", "Balanced", "Modest", "Sustainable"
];

const KEYWORDS_LOW_RISK = [ // Risk < 30%
  "Loyal", "Committed", "Invested", "Passionate", "Engaged", "Anchor", 
  "Thriving", "Proactive", "Advocate", "Leader", "Mentor", "Visionary",
  "Integrated", "Driven", "Enthusiastic", "Bonded", "Key-player", "Champion",
  "Devoted", "Secure", "Embedded", "Aligned", "Motivated", "High-Spirited"
];

/* =========================================================
   2. SENTENCE GENERATOR (Cơ chế sinh câu nhận xét)
   Logic: Job Role + Performance + Random Factor
========================================================= */

const generateAiSummary = (role, score, riskPercent) => {
  const strengths = ["technical consistency", "delivery speed", "team coordination", "complex problem solving", "system architecture", "strategic thinking"];
  const weaknesses = ["soft skills", "documentation", "proactive reporting", "leadership initiative", "mentoring juniors", "cross-team communication"];
  
  // Random chọn 1 điểm mạnh và 1 điểm yếu
  const s = strengths[Math.floor(Math.random() * strengths.length)];
  const w = weaknesses[Math.floor(Math.random() * weaknesses.length)];

  // Kịch bản 1: Giỏi nhưng rủi ro đi cao (High Performer, Flight Risk)
  if (score > 85 && riskPercent > 70) {
    return `Employee demonstrates exceptional ${s}, however, retention risk is critical. Signs of burnout detected despite high output.`;
  }
  
  // Kịch bản 2: Giỏi và gắn bó (Top Talent)
  if (score > 85 && riskPercent <= 30) {
    return `Top Talent identified. Employee shows outstanding ${s} and strong loyalty. Recommended for promotion or leadership training.`;
  }

  // Kịch bản 3: Điểm thấp và rủi ro cao (Low Performer)
  if (score < 60 && riskPercent > 60) {
    return `Critical Alert: Employee is struggling with ${s} and shows high disengagement. Needs immediate intervention regarding ${w}.`;
  }

  // Kịch bản chung (General)
  return `Employee maintains strong consistency in ${s} but needs further improvement in ${w} to reach the next level.`;
};

/* =========================================================
   3. DATA GENERATOR (Mock Data Creator)
========================================================= */

const mockStore = {}; // Cache dữ liệu để không bị đổi khi click đi click lại

const getKeywordsByRisk = (risk) => {
  let source = KEYWORDS_MED_RISK;
  if (risk > 70) source = KEYWORDS_HIGH_RISK;
  if (risk < 30) source = KEYWORDS_LOW_RISK;
  
  // Chọn random 2 từ khóa
  const shuffled = source.sort(() => 0.5 - Math.random());
  return shuffled.slice(0, 2);
};

const generateMockAssessment = (empId, empData) => {
  // Điểm số ngẫu nhiên 50 - 100
  const currentScore = Math.floor(Math.random() * 50) + 50; 
  // Rủi ro nghỉ việc (nghịch đảo điểm số 1 chút + random noise)
  const churnRisk = Math.min(100, Math.max(0, 100 - currentScore + Math.floor(Math.random() * 20 - 10)));
  
  const keywords = getKeywordsByRisk(churnRisk);
  const summary = generateAiSummary(empData?.empJobRole || 'Staff', currentScore, churnRisk);

  return {
    employeeId: empId,
    lastUpdate: new Date().toISOString(),
    overallScore: currentScore, // 0-100
    ranking: currentScore > 90 ? "Top 5%" : (currentScore > 75 ? "Top 20%" : "Average"),
    
    // AI Insights Fields
    prediction: {
      churnProbability: churnRisk, // 0-100
      keywords: keywords, // ["Loyal", "Driven"]
      growthPotential: currentScore > 80 ? "High" : "Moderate",
      summary: summary
    },

    // Mock KPI Data (cho Chart)
    kpis: [
      { name: "Productivity", value: Math.floor(currentScore + Math.random() * 10 - 5) },
      { name: "Quality", value: Math.floor(currentScore + Math.random() * 10 - 5) },
      { name: "Teamwork", value: Math.floor(Math.random() * 40 + 60) }, // Soft skill thường random hơn
      { name: "Adherence", value: 95 }
    ],

    // Mock History
    history: [
      { period: "Q4 2024", score: currentScore, reviewer: "Kevin Martin" },
      { period: "Q3 2024", score: currentScore - Math.floor(Math.random()*5), reviewer: "Kevin Martin" },
      { period: "Q2 2024", score: currentScore + Math.floor(Math.random()*5), reviewer: "Jane Smith" },
    ]
  };
};

/* =========================================================
   4. API METHODS
========================================================= */

export const getAssessmentByEmployee = async (employeeId, employeeDetails = {}) => {
  if (USE_MOCK_API) {
    // Giả lập độ trễ AI Calculation
    await new Promise(r => setTimeout(r, 400));
    
    // Nếu chưa có data thì generate và cache
    if (!mockStore[employeeId]) {
      mockStore[employeeId] = generateMockAssessment(employeeId, employeeDetails);
    }
    return { data: mockStore[employeeId] };
  }
  // API thực tế
  return apiClient.get(`/assessments/${employeeId}`);
};

export default { getAssessmentByEmployee };