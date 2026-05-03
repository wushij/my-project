-- 套餐初始化数据
INSERT INTO `plan` (`name`, `code`, `price`, `original_price`, `credits`, `duration`, `features`, `sort`, `status`) VALUES
    ('Pro', 'PRO', 0.01, 20.00, 2000, 30, '["2,000 Credits", "更多的对话与智能体请求数", "Quest 模式和 Repo Wiki 功能"]', 1, 1),
    ('Pro+', 'PRO_PLUS', 0.02, 60.00, 6000, 30, '["6,000 Credits（总计）", "优先体验新功能"]', 2, 1),
    ('Ultra', 'ULTRA', 0.03, 200.00, 20000, 30, '["20,000 Credits（总计）", "优先体验新功能"]', 3, 1);
