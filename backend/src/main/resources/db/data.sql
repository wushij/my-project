
-- 套餐初始化数据
INSERT INTO `plan` (`name`, `code`, `price`, `original_price`, `credits`, `duration`, `features`, `sort`, `status`) VALUES
('免费', 'FREE', 0.00, NULL, 300, 14, '["\u4e3a\u671f 2 \u5468\u7684 Pro \u8bd5\u7528\u53ca 300 Credits", "\u6709\u9650\u6b21\u4ee3\u7801\u8865\u5168\u4e0e\u7f16\u7801\u9884\u6d4b", "\u6709\u9650\u6b21\u5bf9\u8bdd\u4e0e\u667a\u80fd\u4f53\u8bf7\u6c42\u6570"]', 1, 1),
('Pro', 'PRO', 10.00, 20.00, 2000, 30, '["2,000 Credits", "\u66f4\u591a\u7684\u5bf9\u8bdd\u4e0e\u667a\u80fd\u4f53\u8bf7\u6c42\u6570", "Quest \u6a21\u5f0f\u548c Repo Wiki \u529f\u80fd"]', 2, 1),
('Pro+', 'PRO_PLUS', 30.00, 60.00, 6000, 30, '["6,000 Credits\uff08\u603b\u8ba1\uff09", "\u4f18\u5148\u4f53\u9a8c\u65b0\u529f\u80fd"]', 3, 1),
('Ultra', 'ULTRA', 100.00, 200.00, 20000, 30, '["20,000 Credits\uff08\u603b\u8ba1\uff09", "\u4f18\u5148\u4f53\u9a8c\u65b0\u529f\u80fd"]', 4, 1);
